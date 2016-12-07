package app.psiteportal.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Seminar;
import app.psiteportal.com.model.Transaction;
import app.psiteportal.com.model.User;
import app.psiteportal.com.psiteportal.AddExpenseActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.SeminarProfileActivity;
import app.psiteportal.com.psiteportal.TransactionDetailsActivity;
import app.psiteportal.com.utils.AppController;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> users;
    private Context context;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    static String usertype;

    public UsersAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {

        User t =  users.get(position);

        holder.prof_img.setImageUrl(t.getProf_pic(), imageLoader);
        holder.user_name.setText(t.getFirstname() +" "+ t.getLastname());
        if(t.getActivated().equals("1"))
            holder.member_type.setText("Member");
        else
            holder.member_type.setText("Non-member");
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.registered_in_seminar, viewGroup, false);

        imageLoader = AppController.getInstance().getImageLoader();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "test", Toast.LENGTH_LONG);
            }
        });
        return new UserViewHolder(itemView, context, users);
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView user_name;
        public TextView member_type;
        public User user;
        public NetworkImageView prof_img;

        List<User> users = new ArrayList<>();
        Context context;

        public UserViewHolder(View v, Context context, List<User> users){
            super(v);
            this.users = users;
            this.context = context;
            v.setOnClickListener(this);

            user_name = (TextView) v.findViewById(R.id.user_name);
            member_type = (TextView) v.findViewById(R.id.member_type);
            prof_img = (NetworkImageView) v.findViewById(R.id.prof_pic);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }
    }

    public List<User> getSeminarsList() {
        return users;
    }

//    public void getUserType(String userType){
//        usertype = userType;
//    }
}
