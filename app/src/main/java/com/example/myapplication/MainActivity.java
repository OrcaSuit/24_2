package com.example.myapplication;

import android.os.Bundle;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button domBtn;
    Button saxBtn;
    Button pullBtn;
    Button jsonBtn;

    TextView cityView;
    TextView tempView;
    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab24_2);

        domBtn = (Button) findViewById(R.id.lab2_dom);
        saxBtn = (Button) findViewById(R.id.lab2_sax);
        pullBtn = (Button) findViewById(R.id.lab2_pull);
        jsonBtn = (Button) findViewById(R.id.lab2_json);

        cityView = (TextView) findViewById(R.id.lab2_city);
        tempView = (TextView) findViewById(R.id.lab2_temperature);
        resultView = (TextView) findViewById(R.id.lab2_result_title);

        domBtn.setOnClickListener(this);
        saxBtn.setOnClickListener(this);
        pullBtn.setOnClickListener(this);
        jsonBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == domBtn) {
            domParsing();
        } else if (v == saxBtn) {
            saxParsing();
        } else if (v == pullBtn) {
            pullParsing();
        } else if (v == jsonBtn) {
            jsonParsing();
        }
    }

    private void domParsing() {

        //add~~~~~~~~~~~~~~~~

        try{
            //파일 읽기
            InputStream inputStream = getAssets().open("test.xml");
            //DOM 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream, null);

            //태그 attribute 값 획득
            org.w3c.dom.Element tempElement = (org.w3c.dom.Element)(doc.getElementsByTagName("temperature").item(0));
            String temperature = tempElement.getAttribute("value");

            org.w3c.dom.Element cityElement = (org.w3c.dom.Element)(doc.getElementsByTagName("city").item(0));
            String city = cityElement.getAttribute("name");

            //결과 화면 출력
            resultView.setText("DOM Parsing Result");
            cityView.setText(city);
            tempView.setText(temperature);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saxParsing() {

        //add~~~~~~~~~~~~~~
        resultView.setText("SAX Parsing Result");
        RootElement root = new RootElement("current");
        Element cityElement = root.getChild("city");
        Element tempElement = root.getChild("temperature");

        cityElement.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                cityView.setText(attributes.getValue("name"));
            }
        });

        tempElement.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                tempView.setText(attributes.getValue("value"));
            }
        });

        try {
            InputStream inputStream = getAssets().open("test.xml");
            Xml.parse(inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void pullParsing() {

        //add~~~~~~~~~~~~~~~
        resultView.setText("Pull Parsing Result");
        try{
            InputStream inputStream = getAssets().open("test.xml");
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, null);
            int eventType = parser.getEventType();
            boolean done = false;

            while(eventType != XmlPullParser.END_DOCUMENT && !done){
                String name = null;
                if(eventType == XmlPullParser.START_TAG){
                    name = parser.getName();
                    if(name.equals("city")){
                        cityView.setText(parser.getAttributeValue(null,"name"));
                    } else if (name.equals("temperature")){
                        tempView.setText(parser.getAttributeValue(null, "value"));
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jsonParsing() {
        //add~~~~~~~~~~~~~~~~
    resultView.setText("JSON Parsing Result");
    //파일 읽기
        String json = null;
        try{
            InputStream is = getAssets().open("test.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject root = new JSONObject(json);

            cityView.setText(root.getString("name"));
            JSONObject main = root.getJSONObject("main");
            tempView.setText(main.getString("temp"));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
