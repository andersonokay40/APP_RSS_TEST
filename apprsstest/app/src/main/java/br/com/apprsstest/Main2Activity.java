package br.com.apprsstest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements OnRssLoadListener {


    public class AdapterListView extends BaseAdapter
    {
        private LayoutInflater mInflater;
        private ArrayList<ItemListView> itens;

        public AdapterListView(Context context, ArrayList<ItemListView> itens)
        {
            //Itens que preencheram o listview
            this.itens = itens;
            //responsavel por pegar o Layout do item.
            mInflater = LayoutInflater.from(context);
        }

        /**
         * Retorna a quantidade de itens
         *
         * @return
         */
        public int getCount()
        {
            return itens.size();
        }

        /**
         * Retorna o item de acordo com a posicao dele na tela.
         *
         * @param position
         * @return
         */
        public ItemListView getItem(int position)
        {
            return itens.get(position);
        }

        /**
         * Sem implementação
         *
         * @param position
         * @return
         */
        public long getItemId(int position)
        {
            return position;
        }

        public View getView(int position, View view, ViewGroup parent)
        {
            //Pega o item de acordo com a posção.
            ItemListView item = itens.get(position);
            //infla o layout para podermos preencher os dados
            view = mInflater.inflate(R.layout.mod_listview, null);

            //atravez do layout pego pelo LayoutInflater, pegamos cada id relacionado
            //ao item e definimos as informações.
            ((TextView) view.findViewById(R.id.text)).setText(item.getTexto());
            ((ImageView) view.findViewById(R.id.imagemview)).setImageResource(item.getIconeRid());

            return view;
        }
    }

    public class ItemListView
    {
        private String texto;
        private int iconeRid;

        public ItemListView()
        {
        }

        public ItemListView(String texto, int iconeRid)
        {
            this.texto = texto;
            this.iconeRid = iconeRid;
        }

        public int getIconeRid()
        {
            return iconeRid;
        }

        public void setIconeRid(int iconeRid)
        {
            this.iconeRid = iconeRid;
        }

        public String getTexto()
        {
            return texto;
        }

        public void setTexto(String texto)
        {
            this.texto = texto;
        }
    }


    public ListView listView;
    public AdapterListView adapterListView;
    public ArrayList<ItemListView> itens;
    private String talvez;
    private String urlfeed = "https://endeavor.org.br/?feed=/feed/endeavor-portal"; //url feed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //faz a declaração das views
        bindViews();

        //ação ao clicar nos itens da listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // Toast.makeText(MainActivity.this, "Clicou : " + globalvariaveis.mylist.get(position), Toast.LENGTH_SHORT).show();
                globalvariaveis.selecionado = position;
                Intent intent = new Intent(Main2Activity.this, MainShow.class);
                startActivity(intent);

            }
        });


        //Exibe uma barra de espera + baixa os feeds pela API RSSMANAGER
        Snackbar.make(getWindow().getDecorView().getRootView(), "Loading feeds", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        loadFeeds(urlfeed);


    }

    private void bindViews() {

        listView = (ListView) findViewById(R.id.list);
    }

 //metodo que chama a API RSS
    private void loadFeeds(String url) {
        String[] urlArr = {url};

        new RssReader(Main2Activity.this)
                .showDialog(true)
                .urls(urlArr)
                .parse(this);


    }

    //Retorno quando sucesso do resultado da API RSS
    @Override
    public void onSuccess(List<RssItem> rssItems) {
        try {

            itens = new ArrayList<ItemListView>();
            int i = 0;

            for (RssItem rssItem : rssItems) {

                //prenchimento dos itens da listview
                ItemListView item1 = new ItemListView(rssItem.getTitle(), R.drawable.e);
                itens.add(item1);


                //armazenamento do conteúdo da feed
                talvez = rssItem.getEncoded();
                String requiredString = talvez.substring(talvez.indexOf("<content:encoded>") + 1, talvez.indexOf("</content:encoded>"));
                requiredString = requiredString.substring(16);
                globalvariaveis.mylist.add(i, requiredString);

                i = i + 1;
            }

            //carregamento da listview

            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setItemChecked(1, true);
            //Cria o adapter
            adapterListView = new AdapterListView(this, itens);
            //Define o Adapter
            listView.setAdapter(adapterListView);
            //Cor quando a lista é selecionada para ralagem.
            listView.setCacheColorHint(Color.TRANSPARENT);




        } catch (Exception e){



        }
    }

    //Retorno quando ERRO/Falha da API RSS
    @Override
    public void onFailure(String message) {

        Toast.makeText(Main2Activity.this, "Error:\n" + message, Toast.LENGTH_SHORT).show();
    }



    //Click do 'button/imageview' Refresh
    public void click(View v){

        Snackbar.make(v, "Loading feeds", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        loadFeeds(urlfeed);



    }

    //Click do 'button/imageview' Exit
    public void exit(View v){


        new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Tem certeza que deseja sair do App?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }})
                .setNegativeButton("Não", null).show();


    }
}
