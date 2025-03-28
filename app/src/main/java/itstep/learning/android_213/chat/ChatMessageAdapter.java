package itstep.learning.android_213.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import itstep.learning.android_213.R;
import itstep.learning.android_213.orm.ChatMessage;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {
    private final List<ChatMessage> chatMessages;

    public ChatMessageAdapter(List<ChatMessage> chatMessages){
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatMessageLayoutView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.chat_message, parent, false);

        return new ChatMessageViewHolder(chatMessageLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        holder.setChatMessage(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
}
