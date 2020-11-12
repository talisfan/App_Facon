package pacote.faconapp.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.view.fragments.FragmentHome;
import pacote.faconapp.view.fragments.FragmentInfosPessoais;
import pacote.faconapp.view.fragments.FragmentPerfilPro;

public class Home extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton btnChat;
    private AlertDialog.Builder alertD;
    private Cliente cli;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try {

            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
            context = this;
            alertD = new AlertDialog.Builder(context);
            cli = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);

            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabBar);
            btnChat = findViewById(R.id.btnChat);

            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            ArrayList<String> tabsList = new ArrayList<>();
            tabsList.add("HOME");
            tabsList.add("INFOS PESSOAIS");
            tabsList.add("PROFISSIONAL");

            prepareViewPager(viewPager, tabsList);
            tabLayout.setupWithViewPager(viewPager);

        }catch (Exception ex){
            alertD.setTitle("Error: ");
            alertD.setMessage(ex.getMessage());
            alertD.setNegativeButton("OK", null);
            alertD.show();
        }
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> tabsList) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        FragmentHome fragmentHome = new FragmentHome();
        FragmentInfosPessoais fragmentInfosPessoais = new FragmentInfosPessoais();
        FragmentPerfilPro fragmentPerfilPro = new FragmentPerfilPro();
        bundle.putSerializable(ClassesConstants.CLIENTE, cli);

        fragmentHome.setArguments(bundle);
        fragmentInfosPessoais.setArguments(bundle);
        fragmentPerfilPro.setArguments(bundle);

        adapter.addFragment(fragmentHome, tabsList.get(0));
        adapter.addFragment(fragmentInfosPessoais, tabsList.get(1));
        adapter.addFragment(fragmentPerfilPro, tabsList.get(2));

        viewPager.setAdapter(adapter);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        ArrayList<String> tabsList;
        List<Fragment> fragmentList;

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            tabsList = new ArrayList<>();
            fragmentList = new ArrayList<>();
        }

        public void addFragment(Fragment fragment, String title){
            tabsList.add(title);
            fragmentList.add(fragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabsList.get(position);
        }
    }
}

