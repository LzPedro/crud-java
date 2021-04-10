/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lzpedro.stocksapi.controller;

import com.github.lzpedro.stocksapi.model.Stock;
import com.github.lzpedro.stocksapi.service.StockService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import lombok.var;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author PedroM
 */
@RestController
@RequestMapping("/stock")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    @Autowired
    private StockService stockService;

    @GetMapping
    public ResponseEntity<List<Stock>> findAll() {
        if (stockService.find().isEmpty()) {
            //no stock found
            return ResponseEntity.notFound().build();
        }
        //return all stocks
        return ResponseEntity.ok(stockService.find());
    }
    
    @GetMapping(path = "/{name}", produces = {"application/json"})
    public ResponseEntity<Stock> find(@PathVariable("name") String name) {
        try{
            //return stock by name
            return ResponseEntity.ok(stockService.findByName(name));
        }catch (Exception e) {
            // couldn't find a stock
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping(path = "/{name}", produces = {"application/json"})
    public ResponseEntity<Boolean> delete(@PathVariable("name") String name) {
        try {
            //search and delete stock by name
            stockService.deleteByName(name);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            //couldn't delete a stock
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Stock> create(@RequestBody String stockString) {
        try {
            JSONObject stock = new JSONObject(stockString);
            try {
                if (stockService.isJSONValid(stock.toString())) { //verify if is a valid json
                    //create stock
                    Stock stockCreated = stockService.create(stock);
                    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path(stockCreated.getStockName()).build().toUri();
                    //add stock
                    stockService.add(stockCreated);
                    return ResponseEntity.created(uri).body(null);
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            } catch (Exception e) {
                //JSON fields are not parsable.
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
            }
        } catch (JSONException err) {
            //couldn't create JSON from String
            logger.error("Error", err.toString());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
    }

    @PatchMapping(path = "/{name}", produces = {"application/json"})
    public ResponseEntity<Stock> update(@PathVariable("name") String name, @RequestBody String stockString) {
        try {
            JSONObject stock = new JSONObject(stockString);
            try {
                if (stockService.isJSONValid(stock.toString())) {
                    //find stock by name
                    Stock stockToUpdate = stockService.findByName(name);
                    if (stockToUpdate == null) {
                        //Stock not found
                        return ResponseEntity.notFound().build();
                    } else {
                        //Update stock by name
                        stockToUpdate = stockService.update(stockToUpdate, stock);
                        return ResponseEntity.ok(stockToUpdate);
                    }
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            } catch (Exception e) {
                //JSON fields are not parsable.
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
            }
        } catch (JSONException ex) {
            //couldn't create JSON from String
            java.util.logging.Logger.getLogger(StockController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
    }
}
