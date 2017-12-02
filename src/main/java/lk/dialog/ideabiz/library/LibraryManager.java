package lk.dialog.ideabiz.library;


import lk.dialog.ideabiz.library.APICall.APICall;

public class LibraryManager {

    private static APICall apiCall;

    public static APICall getApiCall() {
        return apiCall;
    }

    public void setApiCall(APICall apiCall) {
        this.apiCall = apiCall;
    }

    public LibraryManager() {

    }
}
