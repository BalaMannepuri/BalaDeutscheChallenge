package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TransferDetails {

  @NotNull
  @NotEmpty (message = "To Account is mandatory for this transaction")
  private final String toAccountId;
  
  @NotNull
  @NotEmpty (message = "From Account is mandatory for this transaction")
  private final String fromAccountId;

  @NotNull
  @Min(value = 0, message = "balance is mandatory for this transaction")
  private BigDecimal balance;


  @JsonCreator
  public TransferDetails(@JsonProperty("toAccountId") String toAccountId,
    @JsonProperty("fromAccountId") String fromAccountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.toAccountId = toAccountId;
    this.fromAccountId = fromAccountId;
    this.balance = balance;
  }
}
