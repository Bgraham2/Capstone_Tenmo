package com.techelevator.tenmo.model;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private int id;

    @Column(name = "transfer_type_id")
    private int transfertypeid;
    @Column(name = "transfer_status_id")
    private int transferstatusid;

    @Column(name = "accountfrom")
    private int accountfrom;
    @Column(name = "accountto")
    private int accountto;
    @Column(name = "amount")
    private BigDecimal amount;


    @ManyToOne()
    @JoinColumn(name = "transfer_type_id", insertable = false, updatable = false)
    private TransferType transferType;

    @ManyToOne
    @JoinColumn(name = "transfer_status_id", insertable = false, updatable = false)
    private TransferStatus transferStatus;

    @Autowired
    public Transfer(int id, int transfertypeid, int transferstatusid, int accountfrom, int accountto, BigDecimal amount) {
        this.id = id;
        this.transfertypeid = transfertypeid;
        this.transferstatusid = transferstatusid;
        this.accountfrom = accountfrom;
        this.accountto = accountto;
        this.amount = amount;
    }


    public Transfer(int transferstatusid, int transfertypeid, int accountto, int accountfrom, BigDecimal amount) {
        this.transferstatusid = transferstatusid;
        this.transfertypeid = transfertypeid;
        this.accountto = accountto;
        this.accountfrom = accountfrom;
        this.amount = amount;
    }

    public Transfer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransfertypeid() {
        return transfertypeid;
    }

    public void setTransfertypeid(int transfertypeid) {
        this.transfertypeid = transfertypeid;
    }

    public int getTransferstatusid() {
        return transferstatusid;
    }

    public void setTransferstatusid(int transferstatusid) {
        this.transferstatusid = transferstatusid;
    }

    public int getAccountfrom() {
        return accountfrom;
    }

    public void setAccountfrom(int accountfrom) {
        this.accountfrom = accountfrom;
    }

    public int getAccountto() {
        return accountto;
    }

    public void setAccountto(int accountto) {
        this.accountto = accountto;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
