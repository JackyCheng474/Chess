import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameServer {
    private static final Game GAME = new Game();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/new", GameServer::handleNew);
        server.createContext("/api/move", GameServer::handleMove);
        server.createContext("/api/state", GameServer::handleState);
        server.start();
        System.out.println("中国象棋后端已启动：http://localhost:8080");
    }

    private static void handleNew(HttpExchange ex) throws IOException {
        sendJson(ex, GAME.newGame());
    }

    private static void handleState(HttpExchange ex) throws IOException {
        sendJson(ex, GAME.toJson());
    }

    private static void handleMove(HttpExchange ex) throws IOException {
        if ("OPTIONS".equals(ex.getRequestMethod())) {
            applyCors(ex);
            ex.sendResponseHeaders(204, -1);
            return;
        }

        String body = new String(ex.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);
        int[] nums = parseMove(body);

        if (nums == null) {
            sendJson(ex, "{\"error\":\"参数格式错误\"}");
            return;
        }

        sendJson(ex, GAME.move(nums[0], nums[1], nums[2], nums[3]));
    }

    private static int[] parseMove(String body) {
        Matcher m = Pattern.compile("\\d+").matcher(body);
        int[] nums = new int[4];
        for (int i = 0; i < 4; i++) {
            if (!m.find()) return null;
            nums[i] = Integer.parseInt(m.group());
        }
        return nums;
    }

    private static void sendJson(HttpExchange ex, String json) throws IOException {
        applyCors(ex);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void applyCors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
}
