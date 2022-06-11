package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;

import io.cucumber.java.bs.A;
import org.springframework.http.*;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class AccountService {
    public static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();

    public HttpEntity makeEntity(){
        HttpHeaders headers = new HttpHeaders();
      //  headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<String>(headers);
    }


    public Account getBalance(Long id){
        var account = new Account();
        try{
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "account/balancebyuserid?id=" + id, HttpMethod.GET, makeEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return account;
    }


    public Account getAccountFromUserId(Long id){
        Account account = new Account();
        try{
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "account/balancebyuserid?id=" + id, HttpMethod.GET, makeEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public List<User> getListOfUsers(String name){
        List<User> userList = new ArrayList<>();
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "user/recipients?username=" + name, HttpMethod.GET, makeEntity(), User[].class );
            userList = Arrays.asList((User[])Objects.requireNonNull((User[])response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return userList;
    }

    public boolean updateBalanceById(Account updatedAccount){

        boolean success = false;
        try {
            restTemplate.put(API_BASE_URL + "account/updatebalance/id?=" + updatedAccount.getUserid(), HttpMethod.PUT, makeEntity(), Account.class);
            success = true;
        }catch (RestClientResponseException | ResourceAccessException ex){
            BasicLogger.log(ex.getMessage());
        }
        return success;
    }

}
