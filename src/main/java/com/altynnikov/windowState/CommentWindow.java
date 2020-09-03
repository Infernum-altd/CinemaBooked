package com.altynnikov.windowState;

import com.altynnikov.model.Comment;
import com.altynnikov.service.DbInteraction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class CommentWindow extends Window {
    private List<Comment> comments;
    private int filmId;

    CommentWindow(int filmId, Socket client, int userId){
        setClient(client);
        this.comments = DbInteraction.getComments(filmId);
        this.filmId = filmId;
        setUserId(userId);
    }

    private void refreshComments(){
        this.comments = DbInteraction.getComments(filmId);
    }

    @Override
    Window nextWindow() {
        return null;
    }

    @Override
    Window previousMenu() {
        return new FilmWindow(getClient(), getUserId());
    }

    String requestToClient() {
        String answer = null;
        try  {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF("1. Просмотреть коментарии\n" +
                    "2. Добавить коментарий\n" +
                    "3. Вернуться назад\n" +
                    "4. Вернуться в главное меню\n" +
                    "Введите число для перехода");
            out.flush();
            DataInputStream in = new DataInputStream(getClient().getInputStream());
            answer = in.readUTF();
        } catch (IOException e){
            e.printStackTrace();
        }
        return answer;
    }

    private Window requestShowComments() throws IOException{
        StringBuilder request = new StringBuilder();
        String answer = null;
        Window nextWindow = null;
        try {
            comments.forEach(x -> {
                request.append(x.getUserName()).append(":\n").append(x.getUserComment()).append("\n");
            });
            request.append("1. Добавить коментарий\n" +
                    "2. Вернуться назад\n" +
                    "3. Вернуться в главное меню\n" +
                    "Введите число для перехода");
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF(request.toString());
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (IOException e){
            e.printStackTrace();
        }

        int inputAction = 0;
        if (answer.matches("\\d")){
            inputAction = Integer.parseInt(answer);
        }

        switch (inputAction){
            case 1:
                requestAddComment();
                nextWindow = this;
                break;
            case 2:
                nextWindow = new FilmWindow(getClient(), getUserId());
                break;
            case 3:
                nextWindow = new UserWindow(getClient(),getUserId());
                break;
            default:
                nextWindow = this;
                new DataOutputStream(getClient().getOutputStream()).writeUTF("illegalInput");
                new DataOutputStream(getClient().getOutputStream()).writeUTF("Несуществующие действие");
                break;
        }
        return nextWindow;
    }

    private void requestAddComment(){
        try {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF("Введите коментарий: ");
            out.flush();
            String comment = new DataInputStream(getClient().getInputStream()).readUTF();
            DbInteraction.addComment(comment, getUserId(), filmId);
            refreshComments();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Window interactWithWindow() throws IOException {
        Window nextWindow = null;
        String answer = requestToClient();

        int inputAction = 0;
        if (answer.matches("\\d")){
            inputAction = Integer.parseInt(answer);
        }

        switch (inputAction){
            case 1:
                nextWindow = requestShowComments();
                break;
            case 2:
                requestAddComment();
                nextWindow = this;
                break;
            case 3:
                nextWindow = new FilmWindow(getClient(), getUserId());
                break;
            case 4:
                nextWindow = new UserWindow(getClient(), getUserId());
                break;
            default:
                nextWindow = this;
                new DataOutputStream(getClient().getOutputStream()).writeUTF("illegalInput");
                new DataOutputStream(getClient().getOutputStream()).writeUTF("Несуществующие действие");
                break;
        }
        return nextWindow;
    }
}
