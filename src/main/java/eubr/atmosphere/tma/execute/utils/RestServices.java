package eubr.atmosphere.tma.execute.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.JsonObject;

import eubr.atmosphere.tma.data.Action;
import eubr.atmosphere.tma.data.Actuator;
import eubr.atmosphere.tma.data.Configuration;

public class RestServices {

    public static void requestRestService(Actuator actuator, Action action) throws IOException {
        // Reference: https://www.baeldung.com/java-http-request
        String configurationString = getJsonObject(action.getConfigurationList());
        URL url = new URL(actuator.getAddress() +
                "?action=" + action.getAction() +
                "&resourceId=" + action.getResourceId() +
                "&configuration=" + configurationString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
          System.out.println(inputLine);
        }
        in.close();

        con.disconnect();
    }

    public static void requestPutRestService(Actuator actuator, Action action) throws IOException {
        // FIXME It would be better to use PATCH instead of PUT.
        //       However, the first experiments did not work, and we decided to move with PUT.
        URL url = new URL(actuator.getAddress() + action.getAction());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        String namespace = "";
        String name = "";
        String replicas = "";

        List<Configuration> confList = action.getConfigurationList();
        for (int i = 0; i < confList.size(); i++) {
            Configuration conf = confList.get(i);
            switch (conf.getKeyName()) {
            case "metadata.namespace":
                namespace = conf.getValue();
                break;
            case "metadata.name":
                name = conf.getValue();
                break;
            case "spec.replicas":
                replicas = conf.getValue();
                break;
            default:
                break;
            }
        }

        String newPayload = "{\n" +
                "  \"metadata\": {\n" +
                "    \"namespace\": \"" + namespace + "\",\n" +
                "    \"name\": \"" + name + "\"\n" +
                "  },\n" +
                "  \"spec\": {\n" +
                "    \"replicas\": " + replicas + "\n" +
                "  }\n" +
                "}";

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(newPayload);
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
          System.out.println(inputLine);
        }
        in.close();

        con.disconnect();
    }

    private static String getJsonObject(List<Configuration> configurationList) {
        JsonObject jsonObject = new JsonObject();
        for (Configuration config: configurationList) {
            jsonObject.addProperty(config.getKeyName(), config.getValue());
        }
        return jsonObject.toString();
    }
}
