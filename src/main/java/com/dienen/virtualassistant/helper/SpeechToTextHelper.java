package com.dienen.virtualassistant.helper;

import com.dienen.virtualassistant.domain.SpeechResponse;
import com.dienen.virtualassistant.util.GoogleResponse;
import com.dienen.virtualassistant.util.Microphone;
import com.dienen.virtualassistant.util.Recognizer;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.Endpoint;
import com.twilio.type.PhoneNumber;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import net.sourceforge.javaflacencoder.FLACFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import com.twilio.rest.api.v2010.account.Call;
import com.twilio.http.TwilioRestClient;
import org.springframework.web.client.RestTemplate;


/**
 * Created by preetraj on 9/15/17.
 */
@Component
public class SpeechToTextHelper {

/*    @Autowired
    private Microphone mic;*/

    @Autowired
    private File micFile;

    @Autowired
    private Recognizer recognizer;

    @Value("${accountSID}")
    private String ACCOUNT_SID;

    @Value("${authToken}")
    private String AUTH_TOKEN;

    @Value("${senderPhoneNumber}")
    private String senderPhoneNumber;

    private static int maxNumOfResponses = 4;

    @Autowired
    private RestTemplate restTemplate;

    private Map<String, String> phoneMap = new HashMap<>();


    private Logger logger = LoggerFactory.getLogger(SpeechToTextHelper.class);

    @PostConstruct
    public void init() throws Exception {
        phoneMap.put("mygirlfriend", "+13125071599");
        phoneMap.put("I", "+16303408025");
        phoneMap.put("mysister", "+13129278526");
    }


    public List<SpeechResponse> getSpeechResponse() throws Exception {
       /* Microphone mic = new Microphone(FLACFileWriter.FLAC);
        mic.captureAudioToFile(micFile);
        File micFile = new File("/tmp/testfile2.flac");

        System.out.println("Recording...");
        Thread.sleep(5000);    //In our case, we'll just wait 5 seconds.
        mic.close();

        List<SpeechResponse> responses = new ArrayList<>();
        //System.out.println("Sample rate is: " + (int) mic.getAudioFormat().getSampleRate());
        GoogleResponse response = recognizer.getRecognizedDataForFlac(micFile, maxNumOfResponses, (int) mic.getAudioFormat().getSampleRate());
        System.out.println("Google Response: " + response.getResponse());
        System.out.println("Google is " + Double.parseDouble(response.getConfidence()) * 100 + "% confident in" + " the reply");
        System.out.println("Other Possible responses are: ");
        for (String s : response.getOtherPossibleResponses()) {
            responses.add(new SpeechResponse(s));
        }
        micFile.deleteOnExit();
        //mic.close();
        return responses;*/
        System.setProperty("google-api-key", "AIzaSyDXpaDCTaJiYQCnuJcUmJUqxEBNu0SI9O4");
        AudioFileFormat.Type[] typeArray = AudioSystem.getAudioFileTypes();
        for (AudioFileFormat.Type type : typeArray) {
            System.out.println("type: " + type.toString());
        }

        Microphone mic = new Microphone(FLACFileWriter.FLAC);
        File file = new File("/tmp/testfile2.flac");    //Name your file whatever you want
        try {
            mic.captureAudioToFile(file);
        } catch (Exception ex) {
            //Microphone not available or some other error.
            System.out.println("ERROR: Microphone is not availible.");
            ex.printStackTrace();
        }


/* User records the voice here. Microphone starts a separate thread so do whatever you want
     * in the mean time. Show a recording icon or whatever.
     */

        try {
            System.out.println("Recording...");
            Thread.sleep(5000);    //In our case, we'll just wait 5 seconds.
            mic.close();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        mic.close();        //Ends recording and frees the resources
        System.out.println("Recording stopped.");

        Recognizer recognizer = new Recognizer(Recognizer.Languages.ENGLISH_US, System.getProperty("google-api-key"));
        //Although auto-detect is available, it is recommended you select your region for added accuracy.
        try {
            int maxNumOfResponses = 4;
            System.out.println("Sample rate is: " + (int) mic.getAudioFormat().getSampleRate());
            GoogleResponse response = recognizer.getRecognizedDataForFlac(file, maxNumOfResponses, (int) mic.getAudioFormat().getSampleRate());
            String res = response.getResponse();
            System.out.println("Google Response: " + res);
            runAnalyzer(res);
            System.out.println("Google is " + Double.parseDouble(response.getConfidence()) * 100 + "% confident in" + " the reply");
            System.out.println("Other Possible responses are: ");
            for (String s : response.getOtherPossibleResponses()) {
                System.out.println("\t" + s);
            }

        } catch (Exception ex) {
            // TODO Handle how to respond if Google cannot be contacted
            System.out.println("ERROR: Google cannot be contacted");
            ex.printStackTrace();
        }

        file.deleteOnExit();

        return null;
    }

    public String runAnalyzer(String text) throws Exception {

        Sentence sent = new Sentence(text);

        switch (sent.posTag(0)) {
            case "VB": {
                switch (sent.lemmas().get(0)) {
                    case "call":
                        String a = "";
                        for (int i = 1; i < sent.lemmas().size(); i++) {
                            a = a.concat("").concat(sent.lemma(i));
                        }
                        String name = phoneMap.get(a);
                        call(name);
                        break;
                }

            }
            break;
            case "NN": {
                //String name = phoneMap.get(sent.lemmas().get(1));
                String a = "";
                for (int i = 1; i < sent.lemmas().size(); i++) {
                    a = a.concat("").concat(sent.lemma(i));
                }
                String name = phoneMap.get(a);
                switch (sent.lemmas().get(0)) {
                    case "text":
                        sendTextMessage(name, "hello");//sent.lemmas().get(1)
                        break;
                    case "message":
                        sendTextMessage(name, "hello");//sent.lemmas().get(1)
                        break;
                    case "search":
                        String src = "";
                        for (int i = 1; i < sent.lemmas().size(); i++) {
                            src = src.concat("'").concat(sent.lemma(i));
                        }
                        src = src.concat("'");
                        search(src);
                        logger.info("");
                        break;
                }

            }
            break;
        }

        return "";
    }

    public void sendTextMessage(String toPhoneNumber, String body) throws URISyntaxException {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(ACCOUNT_SID, new PhoneNumber(toPhoneNumber),
                new PhoneNumber(senderPhoneNumber), body).create();
    }

    public String call(String toPhoneNumber) throws URISyntaxException {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);


        Call call = Call.creator(new PhoneNumber(toPhoneNumber),
                new PhoneNumber(senderPhoneNumber), new URI("http://demo.twilio.com/docs/voice.xml")).create();

        return call.getSid();
    }

    private void search(String searchText) throws Exception {
        searchText = searchText.trim();
        String command = "open https://www.google.com/search?q=" + searchText;
        Runtime.getRuntime().exec(command);
    }

}
