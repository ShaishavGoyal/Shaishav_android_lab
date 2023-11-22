package com.example.shaishavandroidlab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import com.example.shaishavandroidlab.databinding.ActivityChatRoomBinding;
import com.example.shaishavandroidlab.databinding.ReceiveMessageBinding;
import com.example.shaishavandroidlab.databinding.SentMessageBinding;

public class ChatRoom extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    ChatRoomViewModel chatModel ;
    ArrayList<ChatMessage> messages;
    private RecyclerView.Adapter myAdapter;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        ChatMessageDAO mDAO = db.cmDAO();
        switch( item.getItemId() ) {
            case R.id.item_1:
                ChatMessage selectedMessage = chatModel.selectedMessage.getValue();
                if (selectedMessage != null) {
                    int position = messages.indexOf(selectedMessage);
                    if (position != -1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Do you want to delete the message: " + selectedMessage.getMessage())
                                .setTitle("Question:")
                                .setPositiveButton("Yes", (dialogI, cl) -> {
                                    ChatMessage m = messages.get(position);
                                    messages.remove(position);
                                    myAdapter.notifyItemRemoved(position);

                                    Executor thread = Executors.newSingleThreadExecutor();
                                    thread.execute(() -> {
                                        mDAO.deleteMessage(m);
                                    });

                                    Snackbar.make(binding.getRoot(), "You deleted message #" + position, Snackbar.LENGTH_LONG)
                                            .setAction("Undo", snackbar -> {
                                                messages.add(position, m);
                                                myAdapter.notifyItemInserted(position);
                                                Executor thread1 = Executors.newSingleThreadExecutor();
                                                thread1.execute(() -> {
                                                    mDAO.insertMessage(m);
                                                });
                                            })
                                            .show();
                                })
                                .setNegativeButton("No", (dialogI, cl) -> {})
                                .create()
                                .show();
                    }
                }
                break;
            case R.id.item_2:
                Toast.makeText(this, "Version 1.0, created by Shaishav Goyal", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        ChatMessageDAO mDAO = db.cmDAO();
        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.myToolbar);
        EditText textInput=findViewById(R.id.textInput);
        messages = chatModel.messages.getValue();

        if(messages == null)
        {
            chatModel.messages.setValue(messages = new ArrayList<>());
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                messages.addAll( mDAO.getAllMessages() ); //Once you get the data from database
                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }
        if(messages == null)
        {
            chatModel.messages.postValue( messages = new ArrayList<ChatMessage>());
        }

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.sendbutton.setOnClickListener(click->{

            String typedMessage = String.valueOf(textInput.getText());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");

            String currentDateAndTime = sdf.format(new Date());

            ChatMessage chatMessage = new ChatMessage(typedMessage,currentDateAndTime,true);

            synchronized (messages) {
                messages.add(chatMessage);
            }
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(() ->{
                chatMessage.id =(int)  mDAO.insertMessage(chatMessage);//add to database;

            });
            myAdapter.notifyItemInserted(messages.size()-1);
            binding.textInput.setText("");

        });

        binding.receiveButton.setOnClickListener(click->{

            String typedMessage = String.valueOf(textInput.getText());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");

            String currentDateAndTime = sdf.format(new Date());

            ChatMessage chatMessage = new ChatMessage(typedMessage,currentDateAndTime,false);

            synchronized (messages) {
                messages.add(chatMessage);
            }
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(() ->{
                chatMessage.id =(int)  mDAO.insertMessage(chatMessage);//add to database;

            });
            myAdapter.notifyItemInserted(messages.size()-1);
            binding.textInput.setText("");

        });

        binding.recycleView.setAdapter(myAdapter=new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                if (viewType == 0) {
                    // Inflating the send_message layout
                    View view = inflater.inflate(R.layout.sent_message, parent, false);
                    return new MyRowHolder(view);
                } else {
                    // Inflating the receive_message layout
                    View view = inflater.inflate(R.layout.receive_message, parent, false);
                    return new MyRowHolder(view);
                }
            }
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                ChatMessage chatMessage = messages.get(position);
                holder.messageText.setText(chatMessage.getMessage());
                holder.timeText.setText(chatMessage.getTimeSent());
            }
            @Override
            public int getItemCount() {
                return messages.size();
            }
            @Override
            public int getItemViewType(int position){
                ChatMessage chatMessage = messages.get(position);
                if (chatMessage.IsSentButton()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
            ChatMessageDAO mDAO = db.cmDAO();
            itemView.setOnClickListener(clk->{
                int position = getAbsoluteAdapterPosition();
                ChatMessage selected = messages.get(position);

                chatModel.selectedMessage.postValue(selected);

            });
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }
}