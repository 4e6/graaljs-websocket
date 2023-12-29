(function (jvm) {

    globalThis.crypto = {
        subtle : 0,
        randomUUID : function() {
           debugger;
           throw 'randomUUID';
        },
        getRandomValues : function(arr) {
            for (let i = 0; i < arr.length; i++) {
                arr[i] = Math.floor(Math.random() * Number.MAX_SAFE_INTEGER);
            }
            return arr;
        }
    };

    globalThis.WebSocket = function(config) {
        debugger;
        return {};
    };
    globalThis.Buffer = function(config) {
        debugger;
        return {};
    };
    globalThis.Buffer.from = function() {
        debugger;
        return {};
    };
    globalThis.WebSocketServer = function(config) {
        var webSocketServerData = jvm(null, 'new-web-socket-server-data', config);
        var wss = {
            on : function(type, callback) {
               var webSocketData = jvm(webSocketServerData, type, callback);
               var ws = {
                  on : function(type, callback) {
                    jvm(webSocketData, type, callback);
                  },
                  send : function(msg) {
                    jvm(webSocketData, "send", msg);
                  }
               }
               callback(ws);
            }
        };
        return wss;
    };
})
