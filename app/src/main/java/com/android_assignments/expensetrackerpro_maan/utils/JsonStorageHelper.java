package com.android_assignments.expensetrackerpro_maan.utils;

import android.content.Context;

import com.android_assignments.expensetrackerpro_maan.models.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class JsonStorageHelper {
    private static final String SAVE_FILE = "transactions_saved.json";
    private static final String ASSET_FILE = "transactions.json";
    private static List<Transaction> cached = null;

    public static synchronized List<Transaction> getTransactions(Context ctx) {
        if (cached != null) return cached;
        try {
            File saved = new File(ctx.getFilesDir(), SAVE_FILE);
            String json;
            if (saved.exists()) {
                json = readStream(new FileInputStream(saved));
            } else {
                // fallback to initial asset
                InputStream is = ctx.getAssets().open(ASSET_FILE);
                json = readStream(is);
            }
            cached = parseJson(json);
        } catch (Exception e) {
            e.printStackTrace();
            cached = new ArrayList<>();
        }
        return cached;
    }

    public static synchronized void saveTransactions(Context ctx) {
        if (cached == null) return;
        try {
            JSONArray arr = new JSONArray();
            for (Transaction t : cached) {
                JSONObject o = new JSONObject();
                o.put("id", t.id);
                o.put("title", t.title);
                o.put("category", t.category);
                o.put("amount", t.amount);
                o.put("date", t.date);
                o.put("description", t.description);
                // store absolute path or null
                o.put("receiptPath", t.receiptPath == null ? JSONObject.NULL : t.receiptPath);
                o.put("isFavorite", t.isFavorite);
                arr.put(o);
            }
            File out = new File(ctx.getFilesDir(), SAVE_FILE);
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(arr.toString().getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readStream(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private static List<Transaction> parseJson(String json) {
        List<Transaction> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Transaction t = new Transaction();
                t.id = o.optString("id", UUID.randomUUID().toString());
                t.title = o.optString("title", "");
                t.category = o.optString("category", "Miscellaneous");
                t.amount = o.optDouble("amount", 0.0);
                t.date = o.optString("date", "");
                t.description = o.optString("description", "");
                String rp = o.optString("receiptPath", null);
                t.receiptPath = (rp == null || rp.equals("null")) ? null : rp;
                t.isFavorite = o.optBoolean("isFavorite", false);
                list.add(t);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
