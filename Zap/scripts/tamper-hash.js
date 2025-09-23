// tamper-hash.js (Graal.js, type=httpsender)
function sendingRequest(msg, initiator, helper) {
  try {
    var uriObj = msg.getRequestHeader().getURI();
    var path = uriObj.getPath();               // p.ej. "/voting"
    var method = msg.getRequestHeader().getMethod();

    if (path && path.equals("/voting") &&
        method && method.equalsIgnoreCase("POST") &&
        msg.getRequestBody().length() > 0) {

      var bodyStr = msg.getRequestBody().toString();
      print("DEBUG body before: " + bodyStr);

      var json;
      try { json = JSON.parse(bodyStr); } catch (e) {
        print("DEBUG not JSON: " + e);
        return;
      }

      if (json.integrityHash) {
        json.integrityHash = "TAMPERED_HASH_TEST"; 
        var newBody = JSON.stringify(json);

        msg.setRequestBody(newBody);
        msg.getRequestHeader().setHeader("Content-Type", "application/json");
        msg.getRequestHeader().setContentLength(msg.getRequestBody().length());

        print("DEBUG body after:  " + newBody);
      } else {
        print("DEBUG no integrityHash field");
      }
    }
  } catch (e) {
    print("ERROR tamper-hash: " + e);
  }
}

function responseReceived(msg, initiator, helper) { /* no-op */ }
