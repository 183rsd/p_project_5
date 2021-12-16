package com.test.p_project_5;

import android.os.AsyncTask;
        import android.os.Build;

        import androidx.annotation.RequiresApi;

        import org.json.simple.JSONObject;
        import org.json.simple.parser.JSONParser;
        import org.json.simple.parser.ParseException;

        import java.io.BufferedOutputStream;
        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.URL;
        import java.net.URLConnection;
        import java.nio.charset.StandardCharsets;

//현재 사용자 대조

public class ImageAsyncTask_compare extends AsyncTask<String,String,Float> {
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Float doInBackground(String... params) {//background에서 동작한다는메소드 //...은 파라미터가 배열처럼 넘어온다는 뜻 한개도될수있고 여러개될수도있음(가변적
        String result = null;
        Float num = null;
        boolean check=true;

        try {
            URL url = null;
            URLConnection connection = null;
            StringBuilder responseBody = new StringBuilder();
            JSONParser jsonparse = new JSONParser();
            try {
                // URL 지정
                url = new URL("http://svc.saltlux.ai:31781");
                connection = url.openConnection();
                // Header 정보 지정
                connection.addRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject jsonBody = new JSONObject();
                // 사용자 키
                jsonBody.put("key", "9fd1c735-3dc2-4c4d-9410-2b05202705b4");
                // 서비스 ID
                jsonBody.put("serviceId", "01915082929");

                // 서비스에서 필요로 하는 parameter
                JSONObject argument = new JSONObject();
                argument.put("data",params[0]);
                argument.put("regist", false);
//                argument.put("id",params[1]);
                jsonBody.put("argument", argument);

                BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

                bos.write(jsonBody.toJSONString().getBytes(StandardCharsets.UTF_8));
                bos.flush();
                bos.close();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line = null;
                while ((line = br.readLine()) != null) {
                    responseBody.append(line);
                }
                br.close();

            } catch (Throwable e) {
                System.out.println(e.toString());
            }
            result = responseBody.toString();
            try {
                JSONObject jsonarray = (JSONObject) jsonparse.parse(result);
                String i = jsonarray.get("similarity").toString();
                num = Float.parseFloat(i) * 100;
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return num;
    }

    @Override
    protected void onProgressUpdate(String... params){

    }

    @Override
    protected void onPostExecute(Float f){
        super.onPostExecute(f);
    }
}