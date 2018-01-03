package com.example.paul.personalproject;

import com.example.paul.personalproject.SERVER_MODE;
import com.example.paul.personalproject.SERVER_RESPONSE;

/**
 * Created by Paul on 1/2/18.
 */

public interface MasterDataCallback {
    void returnMasterData(String data, SERVER_MODE server_mode, SERVER_RESPONSE server_response);
}
