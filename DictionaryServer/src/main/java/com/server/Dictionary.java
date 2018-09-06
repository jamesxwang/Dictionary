package com.server;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/9/1 15:27
 */
public class Dictionary {

    // key value pairs of dictionary words
    private HashMap<String, List<String>> map;
    // dictionary file path
    private String filePath;

    public Dictionary(String filePath) {
        this.filePath = filePath;
        this.map = new HashMap<>();
        this.readJSONFile(filePath);
    }

    //<editor-fold defaultstate="collapsed"desc="Operations between JSON file and HashMap">
    public synchronized void readJSONFile(String filePath) {
        map.clear();
        JSONParser jsonParser = new JSONParser();
        try {
            Object object = jsonParser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) object;

            //load json into hashMap
            for (Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
                String word = (String) iterator.next();
                JSONArray definition = (JSONArray) jsonObject.get(word);
                List<String> def = new ArrayList<>();
                for (Iterator it = definition.iterator(); it.hasNext();) {
                    def.add((String) it.next());
                }
                map.put(word,def);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void updateJSONFile(String json) {
        try {
            RandomAccessFile file = new RandomAccessFile(this.filePath, "rw");

            // search for "}" in the file
            long pos = file.length();
            while (pos > 0) {
                pos--;
                file.seek(pos);
                if (file.readByte() == '}') {
                    file.seek(pos);
                    break;
                }
            }

            if (pos <= 0) {
                throw new Exception("JSON file cannot be parsed");
            }

            file.writeBytes("," + json + "}");
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // write JSON file from JSONObject
    public synchronized void writeJSONFile(JSONObject jsonObject) {
        try {
            FileWriter file = new FileWriter(this.filePath);
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //</editor-fold>

    public void addWord(String word, List<String> defs) {
        this.map.put(word, defs);

        // create json object and get its json string equivalent
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(word, defs);
        String json = jsonObject.toJSONString();
        json = json.substring(1, json.length() - 1);    // trim curly brace of json object

        this.updateJSONFile(json);
    }

    // delete word
    public void removeWord(String word) {
        List<String> values = this.map.remove(word);
        if (values == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        for (Iterator iterator = this.map.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            List<String> def = this.map.get(key);
            jsonObject.put(key, def);
        }
        this.writeJSONFile(jsonObject);
    }

    public List<String> getDefinitions(String word) {
        return this.map.get(word);
    }

    public boolean hasWord(String word) {
        return this.map.get(word) != null;
    }

}
