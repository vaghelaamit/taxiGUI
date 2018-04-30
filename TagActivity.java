package com.vaghela.amit.sampleapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.AutoLabelUISettings;
import com.dpizarro.autolabel.library.Label;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by AMIT on 30-04-2018.
 */

public class TagActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView tagList, btnList;
    private Button btnSave;
    private EditText name;
    private ArrayList<String> arrTagList = new ArrayList<>();
    private ArrayList<String> arrbtnList = new ArrayList<>();
    private AdapterButtonList adapterButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_view);

        initViews();
    }

    private void initViews() {

        tagList = (RecyclerView) findViewById(R.id.tagList);
        btnList = (RecyclerView) findViewById(R.id.btnList);
        btnSave = (Button) findViewById(R.id.btnSave);

        tagList.setLayoutManager(new LinearLayoutManager(this));
        btnList.setLayoutManager(new LinearLayoutManager(this));

        setData();
    }

    private void setData() {
        arrTagList.clear();
        //Model mn = new Model();
        //mn.setId(0);
        //mn.setName("");
        arrTagList.add("Ride");
        arrTagList.add("Driving");
        arrTagList.add("Baseball");
        arrTagList.add("Hockey");
        arrTagList.add("Food");
        arrTagList.add("Football");
        //arrTagList.add(mn);

        Adapter ad = new Adapter(TagActivity.this, arrTagList);
        tagList.setAdapter(ad);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSave) {
            if (checkValidate()) {
                //insert data database
            }
        }
    }

    private boolean checkValidate() {
        if (TextUtils.isEmpty(name.getText())) {
            return false;
        }
        return false;
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<String> moviesList;
        private Context ctx_;

        public Adapter(Context ctx, List<String> moviesList) {
            this.moviesList = moviesList;
            this.ctx_ = ctx;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row_tagview, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            //Movie movie = moviesList.get(position);

            AutoLabelUISettings autoLabelUISettings =
                    new AutoLabelUISettings.Builder()
                            .withIconCross(R.drawable.cross)
                            .withMaxLabels(6)
                            .withShowCross(true)
                            .withLabelsClickables(true)
                            .withTextColor(android.R.color.black)
                            .withTextSize(R.dimen.label_title_size)
                            .build();

            holder.tagview.setSettings(autoLabelUISettings);

            holder.tagview.addLabel(moviesList.get(position));

            holder.tagview.setOnLabelClickListener(new AutoLabelUI.OnLabelClickListener() {
                @Override
                public void onClickLabel(View v) {
                    String text = ((Label) v).getText();
                    if (arrbtnList.size() < 6) {
                        arrbtnList.add(text);
                        if (adapterButton == null) {
                            adapterButton = new AdapterButtonList(TagActivity.this, arrbtnList);
                            btnList.setAdapter(adapterButton);
                            Toast.makeText(ctx_, ((Label) v).getText(), Toast.LENGTH_SHORT).show();
                        } else {
                            adapterButton.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public AutoLabelUI tagview;

            public MyViewHolder(View view) {
                super(view);
                tagview = (AutoLabelUI) view.findViewById(R.id.label_view);
            }
        }
    }


    public class AdapterButtonList extends RecyclerView.Adapter<AdapterButtonList.MyViewHolder> {

        private List<String> moviesList;
        private Context ctx_;

        public AdapterButtonList(Context ctx, List<String> moviesList) {
            this.moviesList = moviesList;
            this.ctx_ = ctx;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            //Movie movie = moviesList.get(position);
            holder.textView.setText(moviesList.get(position));
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;

            public MyViewHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.textView);
            }
        }
    }
}
