package com.dienen.virtualassistant.config;

import com.dienen.virtualassistant.util.Microphone;
import com.dienen.virtualassistant.util.Recognizer;
import net.sourceforge.javaflacencoder.FLACFileWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created by preetraj on 9/15/17.
 */
@Configuration
public class SpeechConfig {

    @Value("${google-api-key}")
    private String googleApiKey;

    @Bean
    public RestTemplate geRestTemplate(){
        return new RestTemplate();
    }

/*    @Bean
    public Microphone getMicrophone() {
        return new Microphone(FLACFileWriter.FLAC);
    }*/

    @Bean("micFile")
    public File getMicLogs() {
        return new File("/Users/preetraj/Documents/workspace/poc/virtual-assistant/testfile2.flac");
    }

    @Bean
    public Recognizer getRecognizer() {
        return new Recognizer(Recognizer.Languages.ENGLISH_US, googleApiKey);
    }

}
