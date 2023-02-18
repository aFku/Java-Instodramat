package com.rcbg.afku.instodramat.common.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class MetaData {
    private String uri;
    private int statusCode;
    private Instant timestamp;
    private String contentType;

    public MetaData(String uri, int statusCode, String contentType){
        this.timestamp = Instant.now();
        this.uri = uri;
        this.statusCode = statusCode;
        this.contentType = contentType;
    }

    @Override
    public String toString(){
            return "{ \"timestamp\": \"" + this.timestamp + "\", \"uri\": \"" + this.uri + "\", \"statusCode\": \"" + this.timestamp + "\"}";
    }
}
