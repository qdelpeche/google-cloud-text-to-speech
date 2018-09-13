/*
 * Novo Geek - Google Cloud Text to Speech
 *
 * Copyright (c) Gobbo Games. All rights reserved.
 */
package org.novogeek.tts;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1beta1.AudioConfig;
import com.google.cloud.texttospeech.v1beta1.AudioEncoding;
import com.google.cloud.texttospeech.v1beta1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1beta1.SynthesisInput;
import com.google.cloud.texttospeech.v1beta1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1beta1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1beta1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1beta1.VoiceSelectionParams;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author Quinton Delpeche <quinton@novogeek.org>
 */
@SpringBootApplication
public class Application implements CommandLineRunner
{

    /**
     *
     * @param args
     */
    public static void main( String[] args )
    {
        SpringApplication app = new SpringApplication( Application.class );

        try
        {
            app.run( args );
        } //try
        catch( Exception exception )
        {
            System.exit( -1 );
        } //catch
    } //public static void main

    /**
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run( String[] args ) throws Exception
    {
        //This assumes the credentials are in the same folder as the program is running from.
        File file = new File( "credentials.json" );
        
        //Generate the credentials from the file indicated above.
        GoogleCredentials credentials = GoogleCredentials.fromStream( new FileInputStream( file ) ).createScoped( Lists.newArrayList( "https://www.googleapis.com/auth/cloud-platform" ) );

        //Create a fixed credentials provider from the credentials above.
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create( credentials );

        //Create the settings for TextToSpeechClient.
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder().setCredentialsProvider( credentialsProvider ).build();

        if ( settings != null )
        {
            String text = "Hello world from Novo Geek!";
            String outputAudioFilePath = "output.mp3";

            //If we have passed in an argument, we will assume this is the text we want to speak.
            if( args.length > 0 )
            {
                text = args[0];
            } //if
            
            try ( TextToSpeechClient textToSpeechClient = TextToSpeechClient.create( settings ) )
            {
                // Set the text input to be synthesized
                SynthesisInput input = SynthesisInput.newBuilder().setText( text ).build();

                // Build the voice request; languageCode = "en_us"
                VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode( "en-US" ).setSsmlGender( SsmlVoiceGender.MALE ).build();

                // Select the type of audio file you want returned
                AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding( AudioEncoding.MP3 ).build();

                // Perform the text-to-speech request
                SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech( input, voice, audioConfig );

                // Get the audio contents from the response
                ByteString audioContents = response.getAudioContent();

                // Write the response to the output file.
                try ( OutputStream out = new FileOutputStream( outputAudioFilePath ) )
                {
                    out.write( audioContents.toByteArray() );
                    System.out.println( "It is indeed on - file written" );
                } //try
            } //try
        } //if
    } //public void run
} //public class Application

/** *********************************************************************************************************************************************** */
