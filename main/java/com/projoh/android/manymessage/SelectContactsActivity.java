package com.projoh.android.manymessage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SelectContactsActivity extends AppCompatActivity {
    private FloatingActionButton fabAddAll;
    private FloatingActionButton fabAddSome;
    private ArrayAdapter<Person> adapter2;
    private ArrayAdapter<Person> adapter;
    private boolean selected = false;
    public static LinkedHashSet<Person> checkedlist = new LinkedHashSet<>();
    ArrayList<Person> list = new ArrayList<>();
    ArrayList<Person> everysinglecontact = new ArrayList<>();
    Person allcontacts;
    ListView currentcontactsview;
    private EditText inputSearch;
    private ArrayList<Person> temp;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        getSupportActionBar().setTitle("ALL CONTACTS");
        fabAddAll = (FloatingActionButton) findViewById(R.id.fabAddAll2);
        fabAddSome = (FloatingActionButton) findViewById(R.id.fabSome2);

        fabAddAll.hide(false);
        fabAddSome.hide(false);
        fabAddAll.show();
        fabAddSome.show();
        try {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                allcontacts = new Person(name, phoneNumber);
                checkedlist.add(allcontacts);
                everysinglecontact.add(allcontacts);
            }
            phones.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<Person> adapter = new myListAdapter(this, list);
        PreviewAllContacts();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void sendSelected(View view) {
        list.addAll(checkedlist);
        Intent returnIntent = new Intent();
        Set<Person> checkedlist = new HashSet<>();

        for (Person allcontacts : list) {
            if (allcontacts.isSelected()) {
                Person checkedthem = new Person(allcontacts.getName(), allcontacts.getPhone());
                checkedlist.add(checkedthem);
            }
        }
        returnIntent.putExtra("result", (Serializable) checkedlist);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void AddEntireList(View view) {

        for ( int i=0; i < list.size(); i++) {
            list.get(i).setSelected(!selected);
        }
        selected = !selected;
        int theposition = currentcontactsview.getFirstVisiblePosition();
        ArrayAdapter<Person> adapter = new myListAdapter(this, list);
        currentcontactsview.setAdapter(adapter);
        currentcontactsview.setSelection(theposition);
    }

    public void PreviewAllContacts() {
        final FloatingActionButton fabSome = (FloatingActionButton) findViewById(R.id.fabSome2);
        final FloatingActionButton fabAll = (FloatingActionButton) findViewById(R.id.fabAddAll2);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        list.addAll(checkedlist);
        currentcontactsview = (ListView) findViewById(R.id.currentcontactslistview);
        fabAll.attachToListView(currentcontactsview, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fabAll.show();
                fabSome.show();
            }

            @Override
            public void onScrollUp() {
                fabAll.hide();
                fabSome.hide();
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("ListViewFragment", "onScrollStateChanged()");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("ListViewFragment", "onScroll()");
            }
        });
        temp = new ArrayList<>();
        Collections.sort(list, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        adapter = new myListAdapter(this, list);
        adapter2 = new myListAdapter(SelectContactsActivity.this, temp);
        currentcontactsview.setAdapter(adapter);

        final ArrayList<Person> searchedpeople = new ArrayList<>();
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                temp.clear();

                Set<Person> nodup = new HashSet<>();
                nodup.clear();
                String thesearchingtext = inputSearch.getText().toString().toLowerCase();
                int textlength = inputSearch.getText().length();

                if (textlength > 0) {
                    for(Person person : list) {
                        if(person.getName().toLowerCase().contains(thesearchingtext)) {
                            temp.add(person);
                        }
                    }
                    nodup.clear();
                    adapter.clear();
                    nodup.addAll(temp);
                    temp.clear();
                    temp.addAll(nodup);



                    list.clear();
                    list.addAll(temp);
                    Collections.sort(list, new Comparator<Person>() {
                        @Override
                        public int compare(Person o1, Person o2) {
                            return o1.name.compareTo(o2.name);
                        }
                    });
                    currentcontactsview = (ListView) findViewById(R.id.currentcontactslistview);
                    currentcontactsview.setAdapter(adapter);
                } else {
                    list.clear();
                    nodup.clear();
                    nodup.addAll(checkedlist);
                    checkedlist.clear();
                    checkedlist.addAll(nodup);
                    list.addAll(checkedlist);

                    Collections.sort(list, new Comparator<Person>() {
                        @Override
                        public int compare(Person o1, Person o2) {
                            return o1.name.compareTo(o2.name);
                        }
                    });
                    currentcontactsview = (ListView) findViewById(R.id.currentcontactslistview);
                    currentcontactsview.setAdapter(adapter);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SelectContacts Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.android.masstextingapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SelectContacts Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.android.masstextingapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class myListAdapter extends ArrayAdapter<Person> {
        private final Activity context;


        class ViewHolder {
            protected TextView text;
            protected CheckBox checkbox;
            protected TextView number;
            protected ImageView image;
            protected RelativeLayout thelayout;
            protected ImageView checkboxImage;
        }

        public myListAdapter(Activity context, ArrayList<Person> list) {
            super(SelectContactsActivity.this, R.layout.da_item, list);
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.da_item, parent, false);
                holder.thelayout = (RelativeLayout) convertView.findViewById(R.id.theitemlayout);
                holder.image = (ImageView) convertView.findViewById(R.id.contactImage);
                holder.number = (TextView) convertView.findViewById(R.id.number);
                holder.text = (TextView) convertView.findViewById(R.id.name);
                holder.checkboxImage= (ImageView) convertView.findViewById(R.id.checkBoxImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
                holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ColorGenerator generator = ColorGenerator.DEFAULT;
                        int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                        list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                        String thename = list.get(getPosition).getName();
                        String firstLetter = Character.toString(thename.charAt(0));
                        int color = generator.getColor(firstLetter);
                        if(holder.checkbox.isChecked()) {
                            TextDrawable drawable2 = TextDrawable.builder()
                                    .beginConfig()
                                    .textColor(Color.WHITE)
                                    .bold()
                                    .endConfig()
                                    .buildRound(" ", Color.GRAY);
                            holder.checkboxImage.setVisibility(View.VISIBLE);
                            Drawable thecolor = new ColorDrawable(Color.argb(50,173,216,230));
                            holder.thelayout.setBackground(thecolor);
                            holder.image.setImageDrawable(drawable2);

                        } else {
                            TextDrawable drawable2 = TextDrawable.builder()
                                    .beginConfig()
                                    .textColor(Color.BLACK)
                                    .bold()
                                    .endConfig()
                                    .buildRound(firstLetter, color);
                            holder.image.setImageDrawable(drawable2);
                            holder.checkboxImage.setVisibility(View.INVISIBLE);
                            Drawable thecolor = new ColorDrawable(Color.WHITE);
                            holder.thelayout.setBackground(thecolor);
                        }
                    }
                });


                convertView.setTag(holder);
                convertView.setTag(R.id.name, holder.text);
                convertView.setTag(R.id.number, holder.number);
                convertView.setTag(R.id.checkBox, holder.checkbox);
                convertView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        holder.checkbox.setChecked(!holder.checkbox.isChecked());
                    }
                });
                holder.text.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        holder.checkbox.setChecked(!holder.checkbox.isChecked());
                    }
                });
                holder.number.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        holder.checkbox.setChecked(!holder.checkbox.isChecked());

                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setTag(position); // This line is important.

            holder.text.setText(list.get(position).getName());
            holder.number.setText(list.get(position).getPhone());
            holder.checkbox.setChecked(list.get(position).isSelected());
            String thename = list.get(position).getName();
            String firstLetter = Character.toString(thename.charAt(0));
            ColorGenerator generator = ColorGenerator.DEFAULT;
            int color = generator.getColor(firstLetter);

            if (list.get(position).isSelected()) {
                TextDrawable drawable2 = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .bold()
                        .endConfig()
                        .buildRound(" ", Color.GRAY);
                holder.checkboxImage.setVisibility(View.VISIBLE);
                Drawable thecolor = new ColorDrawable(Color.argb(50,173,216,230));
                holder.thelayout.setBackground(thecolor);
            } else {
                TextDrawable drawable2 = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.BLACK)
                        .bold()
                        .endConfig()
                        .buildRound(firstLetter, color);
                holder.image.setImageDrawable(drawable2);
                holder.checkboxImage.setVisibility(View.INVISIBLE);
                Drawable thecolor = new ColorDrawable(Color.WHITE);
                holder.thelayout.setBackground(thecolor);
            }



            return convertView;
        }

    }
}

