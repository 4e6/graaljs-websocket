package org.apidesign.polyfill;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.apidesign.polyfill.crypto.CryptoPolyfill;
import org.apidesign.polyfill.timers.TimersPolyfill;
import org.apidesign.polyfill.websocket.WebSocketPolyfill;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.io.IOAccess;

public class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        var path = "/all-y-websocket.js";
        var demo = Main.class.getResource(path);
        if (demo == null) {
            throw new IOException("Cannot find " + path);
        }
        var commonJsRoot = new File(demo.toURI()).getParent();
        var b = Context.newBuilder("js")
                .allowIO(IOAccess.ALL)
                .allowExperimentalOptions(true)
                .option("js.commonjs-require", "true")
                .option("js.commonjs-require-cwd", commonJsRoot);
        var chromePort = Integer.getInteger("inspectPort", -1);
        if (chromePort > 0) {
            b.option("inspect", ":" + chromePort);
        }
        try (var executor = Executors.newSingleThreadExecutor()) {
            var demoJs = Source.newBuilder("js", demo)
                    .mimeType("application/javascript+module")
                    .build();
            var components = new Polyfill[]{
                new TimersPolyfill(executor),
                new CryptoPolyfill(),
                new WebSocketPolyfill()
            };

            CompletableFuture
                    .supplyAsync(b::build, executor)
                    .thenAcceptAsync(ctx -> {
                        Arrays.stream(components).forEach(c -> c.initialize(ctx));

                        ctx.eval(demoJs);
                    }, executor)
                    .get();

            System.out.println("Press enter to exit");
            System.in.read();
        }
    }

}
