package com.example.mygymcomplete;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<GymMember> memberList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onRenewClick(int position);
    }

    public MemberAdapter(List<GymMember> memberList, OnItemClickListener listener) {
        this.memberList = memberList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        GymMember member = memberList.get(position);
        holder.bind(member, listener);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        private TextView memberNameTextView, memberIdTextView, memberPhoneTextView, memberDatesFTextView,memberDatesLTextView, memberDueTextView;
        private Button renewButton, detailsButton;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberNameTextView = itemView.findViewById(R.id.memberNameTextView);
//            memberIdTextView = itemView.findViewById(R.id.memberIdTextView);
            memberPhoneTextView = itemView.findViewById(R.id.memberPhoneTextView);
            memberDatesFTextView = itemView.findViewById(R.id.memberDatesFTextView);
            memberDatesLTextView = itemView.findViewById(R.id.memberDatesLTextView);
            memberDueTextView = itemView.findViewById(R.id.memberDueTextView);
            renewButton = itemView.findViewById(R.id.renewButton);
            detailsButton = itemView.findViewById(R.id.detailsButton);
        }

        public void bind(final GymMember member, final OnItemClickListener listener) {
            memberNameTextView.setText(member.getFullName());
//            memberIdTextView.setText("ID: " + member.getMemberId());
            memberPhoneTextView.setText("Phone: " + member.getMobileNo());
            memberDatesFTextView.setText("Joining Date: " + member.getStartDate()) ;
            memberDatesLTextView.setText( "Expiry Date : " + member.getDueDate());
            memberDueTextView.setText("Due Amount: " + member.getDueAmount());

            renewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRenewClick(getAdapterPosition());
                }
            });

            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
