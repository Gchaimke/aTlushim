package com.hlt.atlushim;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class HtmlParser {
    String connectToSite(String user, String password, String site) throws IOException {
        HashMap<String, String> formData = new HashMap<>();
        String loginFormUrl = "https://www.tlushim.co.il/login.php";
        formData.put("id_num", user);
        formData.put("password", password);
        Map.Entry<String,String> entry =null;
        Document homePage= null;
        try {
            Connection.Response response  = Jsoup.connect(loginFormUrl)
                    .method(Connection.Method.POST)
                    .referrer("https://www.tlushim.co.il/index.php")
                    .ignoreHttpErrors(true)
                    .data(formData)
                    .followRedirects(true)
                    .execute();
            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println(response.cookies());
            Map<String, String> cookies = response.cookies();

            if(!cookies.isEmpty()) {
                entry = cookies.entrySet().iterator().next();
                String key = entry.getKey();
                String value = entry.getValue();
                homePage = Jsoup
                        .connect(site)
                        .method(Connection.Method.GET)
                        .cookie(key, value)
                        .get();
            }
            return getTableRows(homePage);
        }catch (IOException ex){
            System.out.println("Error"+ex);
        }
        return null;
    }

    private static String getTableRows(Document page) {
        StringBuilder cleanCells = new StringBuilder();
        try {
            Element table = page.select("table[class=atnd]").get(0);
            Elements rows = table.select("tr");
            rows = rows.not("tr[class=atnd_remark_hide]");
            cleanCells.append(table.getElementsByTag("caption").text()).append("\n");
            Element row;
            for (int i = 1; i < rows.size(); i++) {
                StringBuilder tmpString = new StringBuilder();
                for (Element cell : rows.get(i).getAllElements()) {
                    //old cells cleaner : if (!cell.text().isEmpty() && !cell.text().equals(":") &&!cell.text().equals("\r") && !cell.text().equals("\r\n") && !cell.text().equals("\n") && !cell.text().equals(" ") && !cell.text().contains("רגיל") && !cell.text().contains("0.50") &&  !cell.text().contains("8.40") &&  !row.text().contains("שישי") &&  !row.text().contains("שבת") &&  !cell.text().contains("תיאור"))
                    tmpString.append(",").append(cell.text());
                }
                cleanCells.append("\n").append(tmpString);
            }
            return cleanCells.toString(); // old cleaner .replaceAll("(?m)^[ \t]*\r?\n", "");
        }catch (Exception e){
            cleanCells.append("error");
        }

        return cleanCells.toString();
    }
}
