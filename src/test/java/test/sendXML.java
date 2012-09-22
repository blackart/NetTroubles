package test;

import com.myjavatools.xml.BasicXmlData;
import ru.blackart.dsi.infopanel.beans.Group;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class sendXML {
    public static void main(String[] args) throws IOException {
        /*HttpClient client = new DefaultHttpClient();

        URI uriRequest = new URI("http://localhost/send/controller");
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();

        requestParams.add(new BasicNameValuePair("id_trouble", String.valueOf(id_trouble)));
        requestParams.add(new BasicNameValuePair("category", "1"));
        requestParams.add(new BasicNameValuePair("priority", "1"));
        requestParams.add(new BasicNameValuePair("title", title ));
        requestParams.add(new BasicNameValuePair("sw", sw));
        requestParams.add(new BasicNameValuePair("legend", legend));
        requestParams.add(new BasicNameValuePair("services", service));
        requestParams.add(new BasicNameValuePair("desc", desc));
        requestParams.add(new BasicNameValuePair("planupdate", timeout));
        requestParams.add(new BasicNameValuePair("factupdate", date_in));
        requestParams.add(new BasicNameValuePair("factdowndate", date_out));

        uriRequest = URIUtils.createURI(
                uriRequest.getScheme(),
                uriRequest.getHost(),
                uriRequest.getPort(),
                uriRequest.getPath(),
                URLEncodedUtils.format(requestParams, "UTF-8"), null);

        HttpUriRequest aHttpClientRequest = new HttpPost(uriRequest);

        client.execute(aHttpClientRequest);*/        

        BasicXmlData xml = new BasicXmlData("action");
        xml.setAttribute("name","Problem");

        BasicXmlData xml_level_1 = new BasicXmlData("parameters");

        BasicXmlData xml_level_2_category =  new BasicXmlData("parameter");
        xml_level_2_category.setAttribute("name","category");
        xml_level_2_category.setAttribute("value", "1");
        xml_level_1.addKid(xml_level_2_category);

        BasicXmlData xml_level_2_priority =  new BasicXmlData("parameter");
        xml_level_2_priority.setAttribute("name","priority");
        xml_level_2_priority.setAttribute("value", "1");
        xml_level_1.addKid(xml_level_2_priority);

        BasicXmlData xml_level_2_status =  new BasicXmlData("parameter");
        xml_level_2_status.setAttribute("name","status");
        xml_level_2_status.setAttribute("value", "1");
        xml_level_1.addKid(xml_level_2_status);

        xml.addKid(xml_level_1);

        StringBuffer stringBuffer = new StringBuffer();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        baos.toByteArray();



        xml.save(baos);


        System.out.print(xml.getXmlContent().getValue());
    }
}
