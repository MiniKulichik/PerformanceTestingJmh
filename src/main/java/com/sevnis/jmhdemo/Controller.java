package com.sevnis.jmhdemo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @PostMapping("/equalignorecase")
    public ResponseEntity<String> equalIgnoreCase(@RequestBody RequestData requestData) {
        for (String item : requestData.getData()) {
            if (item.equalsIgnoreCase("string500")) {
                return new ResponseEntity<>("Condition met", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/equalonly")
    public ResponseEntity<String> equalOnly(@RequestBody RequestData requestData) {
        for (String item : requestData.getData()) {
            if (item.equals("string500")) {
                return new ResponseEntity<>("Condition met", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok("OK");
    }

}
