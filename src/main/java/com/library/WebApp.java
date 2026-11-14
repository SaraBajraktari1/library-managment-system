package com.library;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WebApp {
    private static final Library library = new Library();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        server.createContext("/", ex -> send(ex, 200, "text/html; charset=utf-8", PAGE));
        server.createContext("/api/books", ex -> action(ex, p -> booksJson()));
        server.createContext("/api/add", ex -> action(ex, p -> {
            library.addBook(new Book(p.get("isbn"), p.get("title"), p.get("author")));
            return "{}";
        }));
        server.createContext("/api/borrow", ex -> action(ex, p -> {
            library.borrowBook(p.get("isbn"), p.get("member"));
            return "{}";
        }));
        server.createContext("/api/return", ex -> action(ex, p -> {
            library.returnBook(p.get("isbn"));
            return "{}";
        }));
        server.createContext("/api/fee", ex -> action(ex, p ->
            "{\"fee\":" + FeeCalculator.calculateFee(Integer.parseInt(p.get("days").trim())) + "}"));
        server.start();
        System.out.println("Open http://localhost:8080 in your browser (Ctrl+C to stop)");
    }

    private static void action(HttpExchange ex, Function<Map<String, String>, String> handler) throws IOException {
        try {
            Map<String, String> params = parse(ex.getRequestURI().getRawQuery());
            params.putAll(parse(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)));
            send(ex, 200, "application/json; charset=utf-8", handler.apply(params));
        } catch (RuntimeException e) {
            send(ex, 400, "application/json; charset=utf-8",
                "{\"error\":\"" + esc(String.valueOf(e.getMessage())) + "\"}");
        }
    }

    private static Map<String, String> parse(String raw) {
        Map<String, String> map = new HashMap<>();
        if (raw == null || raw.isEmpty()) return map;
        for (String pair : raw.split("&")) {
            String[] kv = pair.split("=", 2);
            map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
        }
        return map;
    }

    private static String booksJson() {
        return "[" + library.getBooks().stream().map(b ->
            "{\"isbn\":\"" + esc(b.getIsbn()) + "\",\"title\":\"" + esc(b.getTitle())
            + "\",\"author\":\"" + esc(b.getAuthor()) + "\",\"borrowed\":" + b.isBorrowed()
            + ",\"borrowedBy\":\"" + esc(b.getBorrowedBy() == null ? "" : b.getBorrowedBy()) + "\"}")
            .collect(Collectors.joining(",")) + "]";
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", " ").replace("\r", " ").replace("\t", " ");
    }

    private static void send(HttpExchange ex, int status, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    private static final String PAGE = """
        <!DOCTYPE html>
        <html lang="en"><head><meta charset="UTF-8"><title>Library Management System</title>
        <style>
          body{font-family:Segoe UI,Arial,sans-serif;max-width:900px;margin:30px auto;padding:0 15px;color:#222}
          h1{margin-bottom:0} .card{border:1px solid #ddd;border-radius:8px;padding:15px;margin:15px 0}
          input{padding:7px;margin:3px} button{padding:7px 14px;cursor:pointer}
          table{width:100%;border-collapse:collapse} th,td{border-bottom:1px solid #eee;padding:8px;text-align:left}
          .ok{color:#0a7d2c} .busy{color:#c0392b} #msg{padding:10px;border-radius:6px;display:none}
          .good{background:#e6f6ea} .bad{background:#fdeaea}
        </style></head><body>
        <h1>Library Management System</h1><p>Simple web view of the TDD project</p>
        <div id="msg"></div>

        <div class="card"><h3>Add a book</h3>
          <input id="isbn" placeholder="ISBN"><input id="title" placeholder="Title">
          <input id="author" placeholder="Author"><button onclick="addBook()">Add</button></div>

        <div class="card"><h3>Borrow / Return</h3>
          <input id="bIsbn" placeholder="ISBN"><input id="member" placeholder="Member ID">
          <button onclick="borrow()">Borrow</button>
          <button onclick="giveBack()">Return</button></div>

        <div class="card"><h3>Late fee calculator</h3>
          <input id="days" type="number" placeholder="Days borrowed">
          <button onclick="fee()">Calculate</button> <b id="feeOut"></b></div>

        <div class="card"><h3>Books</h3>
          <table><thead><tr><th>ISBN</th><th>Title</th><th>Author</th><th>Status</th></tr></thead>
          <tbody id="rows"></tbody></table></div>

        <script>
        const v = id => document.getElementById(id).value;
        function show(text, good){const m=document.getElementById('msg');
          m.textContent=text;m.className=good?'good':'bad';m.style.display='block';}
        async function call(url, params){
          const r = await fetch(url, {method:'POST', body:new URLSearchParams(params)});
          const data = await r.json();
          if(!r.ok){show(data.error,false);return null;}
          return data;
        }
        async function addBook(){
          if(await call('/api/add',{isbn:v('isbn'),title:v('title'),author:v('author')})){
            show('Book added',true);load();}}
        async function borrow(){
          if(await call('/api/borrow',{isbn:v('bIsbn'),member:v('member')})){
            show('Book borrowed',true);load();}}
        async function giveBack(){
          if(await call('/api/return',{isbn:v('bIsbn')})){show('Book returned',true);load();}}
        async function fee(){
          const d = await call('/api/fee',{days:v('days')});
          if(d) document.getElementById('feeOut').textContent = 'Fee: ' + d.fee.toFixed(2) + ' EUR';}
        async function load(){
          const books = await (await fetch('/api/books',{method:'POST'})).json();
          const body = document.getElementById('rows'); body.innerHTML='';
          for(const b of books){
            const tr = body.insertRow();
            [b.isbn,b.title,b.author].forEach(t => tr.insertCell().textContent = t);
            const s = tr.insertCell();
            s.textContent = b.borrowed ? 'Borrowed by ' + b.borrowedBy : 'Available';
            s.className = b.borrowed ? 'busy' : 'ok';
          }}
        load();
        </script></body></html>
        """;
}