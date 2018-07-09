package edu.buaa.server.dataLayer.domain;

import java.math.BigInteger;
import java.sql.Timestamp;

public class TransferPatent {
    public BigInteger id;
    public Double price;
    public String content;
    public Timestamp created_at;
    public BigInteger resource_id;
    public BigInteger from_id;
    public BigInteger to_id;
}
