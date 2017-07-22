package com.gk.erp.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

//import com.android.volley;
/**
 * Created by pc_home on 2016/11/30.
 */

public class CustomRequest extends Request<JSONObject>{

    private Listener<JSONObject> listener;
    private Map<String,String> params;

    public CustomRequest(String url, Map<String,String> params, Listener<JSONObject> responseListener, ErrorListener errorListener) {
       this(Method.POST,url,params,responseListener,errorListener);

    }

    public CustomRequest(int method,String url,Map<String,String> params,Listener<JSONObject> responseListener, ErrorListener errorListener){
        super(method, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, "utf-8");
            return Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}
