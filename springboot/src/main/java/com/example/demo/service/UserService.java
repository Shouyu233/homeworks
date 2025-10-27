package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.db.User;
import com.example.demo.db.UserRepository;
import com.example.demo.db.UserRepositoryFactory;
import com.example.demo.service.dto.TransferRequest;

@Service
public class UserService {
    private final UserRepositoryFactory factory;

    public UserService(UserRepositoryFactory factory) {
        this.factory = factory;
    }

    public Optional<User> findById(long id) throws Exception {
        UserRepository repo = factory.get((String) null);
        return repo.findById(id);
    }

    public java.util.List<User> findAll() throws Exception {
        UserRepository repo = factory.get((String) null);
        return repo.findAll();
    }

    public long create(User u) throws Exception {
        UserRepository repo = factory.get((String) null);
        return repo.insert(u);
    }

    public boolean update(long id, User u) throws Exception {
        UserRepository repo = factory.get((String) null);
        return repo.update(id, u);
    }

    public boolean delete(long id) throws Exception {
        UserRepository repo = factory.get((String) null);
        return repo.delete(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transfer(TransferRequest req) throws Exception {
        // delegate to new method and ignore return
        transferAndReturn(req);
    }

    @Transactional(rollbackFor = Exception.class)
    public com.example.demo.service.dto.TransferResponse transferAndReturn(TransferRequest req) throws Exception {
        com.example.demo.service.dto.TransferResponse resp = new com.example.demo.service.dto.TransferResponse();
        resp.setFromUserId(req.getFromUserId());
        resp.setToUserId(req.getToUserId());
        resp.setAmount(req.getAmount());

        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            resp.setStatus("FAILED");
            resp.setMessage("amount must be positive");
            return resp;
        }
        if (req.getFromUserId() == req.getToUserId()) {
            resp.setStatus("FAILED");
            resp.setMessage("cannot transfer to the same account");
            return resp;
        }
        UserRepository repo = factory.get((String) null);

        // load sender
        Optional<User> fromOpt = repo.findById(req.getFromUserId());
        Optional<User> toOpt = repo.findById(req.getToUserId());
        if (fromOpt.isEmpty()) {
            resp.setStatus("FAILED");
            resp.setMessage("from user not found");
            return resp;
        }
        if (toOpt.isEmpty()) {
            resp.setStatus("FAILED");
            resp.setMessage("to user not found");
            return resp;
        }
        User from = fromOpt.get();
        User to = toOpt.get();

        java.math.BigDecimal fromBal = from.getBalance() == null ? java.math.BigDecimal.ZERO : from.getBalance();
        java.math.BigDecimal toBal = to.getBalance() == null ? java.math.BigDecimal.ZERO : to.getBalance();

        if (fromBal.compareTo(req.getAmount()) < 0) {
            resp.setStatus("FAILED");
            resp.setMessage("insufficient funds");
            resp.setFromBalance(fromBal);
            resp.setToBalance(toBal);
            return resp;
        }

        from.setBalance(fromBal.subtract(req.getAmount()));
        to.setBalance(toBal.add(req.getAmount()));

        boolean u1 = repo.update(from.getId(), from);
        if (!u1) {
            resp.setStatus("FAILED");
            resp.setMessage("failed to update sender");
            return resp;
        }
        boolean u2 = repo.update(to.getId(), to);
        if (!u2) {
            resp.setStatus("FAILED");
            resp.setMessage("failed to update receiver");
            return resp;
        }

        resp.setStatus("OK");
        resp.setMessage("transfer ok");
        resp.setFromBalance(from.getBalance());
        resp.setToBalance(to.getBalance());
        return resp;
    }
}
