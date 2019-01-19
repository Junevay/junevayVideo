package com.junevay.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: junevay
 * @Date: 2019/1/6 23:14
 */
public class ClearMp4Audio {
    private String ffmpegEXE;

    public ClearMp4Audio(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,String videoOutputPath) throws Exception {
//	F:\ffmpeg\bin\ffmpeg.exe -i C:\Users\LJWA\Downloads\Video\asd.mp4  -c:v copy -an  C:\Users\LJWA\Downloads\Video\asd.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-c:v");
        command.add("copy");

        command.add("-an");
        command.add(videoOutputPath);

//		for (String c : command) {
//			System.out.print(c + " ");
//		}

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ( (line = br.readLine()) != null ) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }

    }

    public static void main(String[] args) {
          ClearMp4Audio ffmpeg = new ClearMp4Audio("F:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffmpeg.convertor("C:\\Users\\LJWA\\Downloads\\Video\\asd.mp4","asd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
