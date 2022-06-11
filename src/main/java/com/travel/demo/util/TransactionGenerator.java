package com.travel.demo.util;


public class TransactionGenerator {
    public static String generate(String contractId) {
        return contractId + java.util.UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
