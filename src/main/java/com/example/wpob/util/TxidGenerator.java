package com.example.wpob.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope
public class TxidGenerator {

    private String txid;


    public TxidGenerator() {
        this.txid = createTxid();
    }


    public String getTxid() {
        return txid;
    }

    private String createTxid() {
        // 요청 트랜잭션 ID 생성
        return UUID.randomUUID().toString();
    }
}
