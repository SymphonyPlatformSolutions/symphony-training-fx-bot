package com.symphony.certification.fxbot.command.dataservice;


import com.symphony.certification.fxbot.command.model.InternalQuote;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Getter
@Service
public class DataService {

    public ArrayList<InternalQuote> quotes;

    public DataService(){
        this.quotes = new ArrayList<InternalQuote>();
    }

    public ArrayList<InternalQuote> addQuote(InternalQuote quote){
        this.quotes.add(quote);
        return this.quotes;
    }
}
