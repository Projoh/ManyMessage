package com.projoh.android.manymessage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    ArrayList<Person> currentSendList = new ArrayList<>();
    ArrayList<Person> everysinglecontact = new ArrayList<>();
    Set<Person> noDuplicates = new HashSet<>();
    ArrayAdapter<Person> adapter2;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private AHBottomNavigation bottomNavigation;
    private ViewPager viewPager;
    private FloatingActionButton fabAddAll;
    private FloatingActionButton fabSome;
    private FloatingActionButton fabSubtract;
    private FloatingActionButton fabConfirm;
    private FloatingActionButton fabCancel;
    private FloatingActionButton fabSend;
    private boolean addedallcontacts = false;
    private boolean buttonsShowing = false;
    private boolean contactsexist = false;
    private boolean anyselected = false;
    private int amountofcontacts = 0;
    private ListView currentListView;
    private SearchView mSearchView;
    private ArrayList<Person> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);

        CheckPermissions();
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Max Text");
        getSupportActionBar().setSubtitle("Text all your buds!");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new com.projoh.android.manymessage.CustomPagerAdapter(this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.setCurrentItem(position);
                if (position == 0) {
                    getSupportActionBar().setTitle("Selected Contacts");
                    getSupportActionBar().setSubtitle("Your currently selected Contacts");
                    fabSome = (FloatingActionButton) findViewById(R.id.fabSome);
                    fabSome.show();
                } else {
                    getSupportActionBar().setTitle("Message");
                    getSupportActionBar().setSubtitle("Craft your message with your customized variables!");
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });


        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Contacts", R.drawable.ic_contact_phone_black_18dp, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Message", R.drawable.ic_texticon, R.color.colorPrimaryDark);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.setNotification("0", 0);
        bottomNavigation.setNotification("", 1);
        bottomNavigation.setForceTitlesDisplay(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                if (wasSelected) {
                    viewPager.setCurrentItem(position);
                    return;
                }
                viewPager.setCurrentItem(position);
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle("Selected Contacts");
                        getSupportActionBar().setSubtitle("Your currently selected Contacts");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Message");
                        getSupportActionBar().setSubtitle("Craft your message with your customized variables!");
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            viewPager.setCurrentItem(1);
            bottomNavigation.setCurrentItem(1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void CheckPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS};
            final int REQUEST_CONTACTS = 1;
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }

    }

    public void AddAllContacts(View view) {
        CheckPermissions();

        if (!addedallcontacts) {
            fabSubtract = (FloatingActionButton) findViewById(R.id.fabminus);
            fabSubtract.hide(false);
            fabSubtract.setVisibility(view.VISIBLE);
            fabAddAll = (FloatingActionButton) findViewById(R.id.fabAddAll);
            fabAddAll.hide();
            fabSubtract.show();
            addedallcontacts = true;
            buttonsShowing = false;
            currentSendList.clear();
            noDuplicates.clear();
            try {
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Person entirecontacts = new Person(name, phoneNumber);
                    currentSendList.add(entirecontacts);
                    everysinglecontact.add(entirecontacts);
                }
                phones.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            noDuplicates.addAll(currentSendList);
            currentSendList.clear();
            currentSendList.addAll(noDuplicates);
            amountofcontacts = currentSendList.size();
            Collections.sort(currentSendList, new Comparator<Person>() {
                @Override
                public int compare(Person o1, Person o2) {
                    return o1.name.compareTo(o2.name);
                }
            });
            AddToTheList(currentSendList);
        }

    }

    public void ShowAllContacts(View view) {
        CheckPermissions();
        OpenContactsActivity(view);
        fabAddAll = (FloatingActionButton) findViewById(R.id.fabAddAll);
        fabCancel = (FloatingActionButton) findViewById(R.id.fabminus);
        fabAddAll.hide(false);
        fabAddAll.setVisibility(view.VISIBLE);
        fabCancel.setVisibility(view.VISIBLE);
        addedallcontacts = true;
        fabAddAll.show();
        fabCancel.show();
        buttonsShowing = true;
    }

    public void OpenContactsActivity(View view) {
        Intent intent = new Intent(this, SelectContactsActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                contactsexist = true;
                Set<Person> myObject = (HashSet) data.getSerializableExtra("result");
                ;
                for (Person checkedthem : myObject) {
                    Person human = new Person(checkedthem.getName(), checkedthem.getPhone());
                    currentSendList.add(human);
                }

                noDuplicates.addAll(currentSendList);
                currentSendList.clear();
                currentSendList.addAll(noDuplicates);
                amountofcontacts = currentSendList.size();
                Collections.sort(currentSendList, new Comparator<Person>() {
                    @Override
                    public int compare(Person o1, Person o2) {
                        return o1.name.compareTo(o2.name);
                    }
                });
                AddToTheList(currentSendList);
                addedallcontacts = false;

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public void Confirmation(View view) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                1);
        fabConfirm = (FloatingActionButton) findViewById(R.id.fabconfirm);
        fabCancel = (FloatingActionButton) findViewById(R.id.fabcancel);
        fabSend = (FloatingActionButton) findViewById(R.id.fabsend);
        fabCancel.hide(false);
        fabSend.hide(false);
        fabCancel.setVisibility(view.VISIBLE);
        fabSend.setVisibility(view.VISIBLE);
        fabConfirm.hide();
        fabCancel.show();
        fabSend.show();
    }

    public void cancelsend(View view) {
        fabConfirm = (FloatingActionButton) findViewById(R.id.fabconfirm);
        fabCancel = (FloatingActionButton) findViewById(R.id.fabcancel);
        fabSend = (FloatingActionButton) findViewById(R.id.fabsend);
        fabCancel.hide();
        fabSend.hide();
        fabConfirm.show();
    }

    public void sendtext(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    1);
        }
        fabConfirm = (FloatingActionButton) findViewById(R.id.fabconfirm);
        fabCancel = (FloatingActionButton) findViewById(R.id.fabcancel);
        fabSend = (FloatingActionButton) findViewById(R.id.fabsend);
        fabCancel.hide();
        fabSend.hide();
        fabConfirm.show();
        EditText theMessageBox = (EditText) findViewById(R.id.messageEditText);
        EditText customoneBox1 = (EditText) findViewById(R.id.customtext1);
        EditText customoneBox2 = (EditText) findViewById(R.id.customtext2);
        String themessage = theMessageBox.getText().toString();
        String custommessage1 = customoneBox1.getText().toString();
        String custommessage2 = customoneBox2.getText().toString();

        for (int i = 0; i < currentSendList.size(); i++) {
            themessage = themessage
                    .replace("fname", currentSendList.get(i).getFname())
                    .replace("lname", currentSendList.get(i).getLname())
                    .replace("pnumber", currentSendList.get(i).getPhone())
                    .replace("cone1", custommessage1)
                    .replace("cone2", custommessage2);

            SmsManager.getDefault().sendTextMessage(currentSendList.get(i).getPhone(), null, themessage, null, null);
        }


        Toast.makeText(getApplicationContext(), "Sent the text to " + amountofcontacts + " people", Toast.LENGTH_LONG).show();
    }

    public void RemoveAllContacts(View view) {
        fabSubtract = (FloatingActionButton) findViewById(R.id.fabminus);
        fabAddAll = (FloatingActionButton) findViewById(R.id.fabAddAll);
        fabSubtract.hide(false);
        fabSubtract.show();
        noDuplicates.clear();
        int theposition = 0;
        if (currentListView.getFirstVisiblePosition() == 0) {
            theposition = currentListView.getFirstVisiblePosition();
        }

        for (Person allcontacts : currentSendList) {
            Person checkedthem = new Person(allcontacts.getName(), allcontacts.getPhone());
            noDuplicates.add(checkedthem);
            if (allcontacts.isSelected()) {
                anyselected = true;
                allcontacts.setSelected(false);
                noDuplicates.remove(checkedthem);
            }
        }


        currentSendList.clear();
        currentSendList.addAll(noDuplicates);
        amountofcontacts = currentSendList.size();
        Collections.sort(currentSendList, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.name.compareTo(o2.name);
            }
        });

        if (!anyselected) {
            noDuplicates.clear();
            currentSendList.clear();

        }
        fabAddAll.show();
        addedallcontacts = false;
        anyselected = false;
        AddToTheList(currentSendList);
        currentListView.setSelection(theposition);
        fabSome = (FloatingActionButton) findViewById(R.id.fabSome);
        fabSome.show();
    }

    public void AddToTheList(ArrayList thelist) {
        fabSome = (FloatingActionButton) findViewById(R.id.fabSome);
        fabSubtract = (FloatingActionButton) findViewById(R.id.fabminus);
        fabAddAll = (FloatingActionButton) findViewById(R.id.fabAddAll);
        contactsexist = true;
        amountofcontacts = currentSendList.size();
        bottomNavigation.setNotification(Integer.toString(amountofcontacts), 0);
        currentListView = (ListView) findViewById(R.id.currentlistviewsend);
        ArrayAdapter<Person> adapter2 = new MainListAdapter(this, thelist);
        fabSome.attachToListView(currentListView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fabSubtract.show();
                fabSome.show();
                if(!addedallcontacts) {
                    fabAddAll.show();
                }
            }

            @Override
            public void onScrollUp() {
                fabAddAll.hide();
                fabSubtract.hide();
                fabSome.hide();
            }
        });
        currentListView.setAdapter(adapter2);
        Toast.makeText(getApplicationContext(), "adding to the list!", Toast.LENGTH_SHORT).show();
    }

    public class MainListAdapter extends ArrayAdapter<Person> {
        private final Activity context;


        public MainListAdapter(Activity context, ArrayList<Person> lista) {
            super(MainActivity.this, R.layout.da_item, lista);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.da_item, parent, false);
                holder.thelayout = (RelativeLayout) convertView.findViewById(R.id.theitemlayout);
                holder.image = (ImageView) convertView.findViewById(R.id.contactImage);
                holder.number = (TextView) convertView.findViewById(R.id.number);
                holder.text = (TextView) convertView.findViewById(R.id.name);
                holder.checkboxImage = (ImageView) convertView.findViewById(R.id.checkBoxImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
                holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ColorGenerator generator = ColorGenerator.MATERIAL;
                        int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                        currentSendList.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                        String thename = currentSendList.get(getPosition).getName();
                        String firstLetter = Character.toString(thename.charAt(0));
                        int color = generator.getColor(firstLetter);
                        if (holder.checkbox.isChecked()) {
                            TextDrawable drawable2 = TextDrawable.builder()
                                    .beginConfig()
                                    .textColor(Color.WHITE)
                                    .bold()
                                    .endConfig()
                                    .buildRound(" ", Color.GRAY);
                            holder.checkboxImage.setVisibility(View.VISIBLE);
                            Drawable thecolor = new ColorDrawable(Color.argb(20, 225, 255, 255));
                            holder.thelayout.setBackground(thecolor);
                            holder.image.setImageDrawable(drawable2);

                        } else {
                            TextDrawable drawable2 = TextDrawable.builder()
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

            holder.text.setText(currentSendList.get(position).getName());
            holder.number.setText(currentSendList.get(position).getPhone());
            holder.checkbox.setChecked(currentSendList.get(position).isSelected());
            String thename = currentSendList.get(position).getName();
            String firstLetter = Character.toString(thename.charAt(0));
            ColorGenerator generator = ColorGenerator.DEFAULT;
            int color = generator.getColor(firstLetter);

            if (currentSendList.get(position).isSelected()) {
                TextDrawable drawable2 = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .bold()
                        .endConfig()
                        .buildRound(" ", Color.GRAY);
                holder.checkboxImage.setVisibility(View.VISIBLE);
                holder.image.setImageDrawable(drawable2);
                Drawable thecolor = new ColorDrawable(Color.argb(20, 225, 255, 255));
                holder.thelayout.setBackground(thecolor);
            } else {
                TextDrawable drawable2 = TextDrawable.builder()
                        .buildRound(firstLetter, color);
                holder.image.setImageDrawable(drawable2);
                holder.checkboxImage.setVisibility(View.INVISIBLE);
                Drawable thecolor = new ColorDrawable(Color.WHITE);
                holder.thelayout.setBackground(thecolor);
            }


            return convertView;
        }

        class ViewHolder {
            protected TextView text;
            protected CheckBox checkbox;
            protected TextView number;
            protected ImageView image;
            protected RelativeLayout thelayout;
            protected ImageView checkboxImage;
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

}
