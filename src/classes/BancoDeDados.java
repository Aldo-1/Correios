/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Matheus Nunes
 */
public class BancoDeDados {

    // ATRIBUTOS
    // O mapa de estados ira salvar os estados de acordo com o seus respectivos nomes
    private HashMap<String, Estado> mapDeEstados;
    // O mapa de Logradouros ira salvar cada logradouro de acordo com o cep
    private ConcurrentHashMap<String, Logradouro> mapDeLogradouros;
    private LeitorUnidadeFederal leitorUF;
    private LeitorDeCidades leitorCidade;
    private LeitorDeBairros leitorBairros;
    private LeitorDeLogradouros leitorLogradouros;

    // CONSTRUTOR
    public BancoDeDados() throws IOException {
        this.mapDeEstados = new HashMap<>();
        this.mapDeLogradouros = new ConcurrentHashMap<>();
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retorno = chooser.showOpenDialog(null);
        File arquivoAtual = chooser.getSelectedFile();
        if (retorno == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(null, "Programa finalizado!");
            System.exit(0);
        }
        try {
            this.leitorUF = new LeitorUnidadeFederal(arquivoAtual);
            this.leitorCidade = new LeitorDeCidades(arquivoAtual);
            this.leitorBairros = new LeitorDeBairros(arquivoAtual);
            this.leitorLogradouros = new LeitorDeLogradouros(arquivoAtual, this);
            this.lerArquivos();
        } catch (IOException ex) {
            throw ex;
        }

    }

    // GETTERS
    public HashMap<String, Estado> getMapDeEstados() {
        return mapDeEstados;
    }

    public ConcurrentHashMap<String, Logradouro> getMapDeLogradouros() {
        return mapDeLogradouros;
    }

    // M�TODOS
    public void cadastrarEstado(Estado estado) {
        this.mapDeEstados.put(estado.getSiglaEstado(), estado);
    }

    public String buscarCidadesDaUF(String sigla) {
        StringBuilder string = new StringBuilder();
        string.append("\n");
        Estado estado = this.mapDeEstados.get(sigla);

        if (estado == null) {
            return "\nN�o existe estado com essa sigla!\n";
        }
        int cont = 1;
        for (Cidade cidade : estado.getCidades().values()) {
            string.append(Integer.toString(cont)).append(" - ").append(cidade.getNomeCidade()).append("\n");
            cont++;
        }
        return string.toString();
    }

    public String buscarBairrosDaCidade(String UF, String nomeCidade) {
        StringBuilder string = new StringBuilder();
        string.append("\n");

        Estado estado = this.mapDeEstados.get(UF);
        Cidade cidade = null;

        if (estado == null) {
            return "\nN�o existe estado com essa sigla!\n";
        } else {
            for (Cidade cidadeNoBanco : estado.getCidades().values()) {
                if (nomeCidade.compareToIgnoreCase(cidadeNoBanco.getNomeCidade()) == 0) {
                    cidade = estado.getCidades().get(cidadeNoBanco.getNomeCidade());
                    break;
                }
            }
            if (cidade == null) {
                return "\nN�o existe neste estado cidade com esse nome!\n";
            } else {
                int cont = 1;
                for (Bairro bairro : cidade.getBairros().values()) {
                    string.append(Integer.toString(cont)).append(" - ").append(bairro.getNomeBairro()).append("\n");
                    cont++;
                }
                return string.toString();
            }
        }
    }

    public String buscarLogradourosDoBairro(String UF, String nomeCidade, String nomeBairro) {
        StringBuilder string = new StringBuilder();
        string.append("\n");

        Estado estado = this.mapDeEstados.get(UF);
        Cidade cidade = null;
        Bairro bairro = null;

        if (estado == null) {
            return "\nN�o existe estado com essa sigla!\n";
        } else {
            for (Cidade cidadeNoBanco : estado.getCidades().values()) {
                if (nomeCidade.compareToIgnoreCase(cidadeNoBanco.getNomeCidade()) == 0) {
                    cidade = estado.getCidades().get(cidadeNoBanco.getNomeCidade());
                    break;
                }
            }
            if (cidade == null) {
                return "\nN�o existe neste estado cidade com esse nome!\n";

            } else {
                for (Bairro bairroNoBanco : cidade.getBairros().values()) {
                    if (nomeBairro.compareToIgnoreCase(bairroNoBanco.getNomeBairro()) == 0) {
                        bairro = cidade.getBairros().get(bairroNoBanco.getNomeBairro());
                        break;
                    }
                }
                if (bairro == null) {
                    return "\nN�o existe neste bairro cidade com esse nome!\n";
                } else {

                    int cont = 1;
                    for (Logradouro logradouro : bairro.getLogradouros().values()) {
                        string.append(Integer.toString(cont)).append(" - ").append(logradouro.getNomeLogradouro()).append("\n");
                        cont++;
                    }
                    return string.toString();
                }
            }
        }
    }

    public String pegarLogradouroPeloCEP(String CEP) {
        StringBuilder string = new StringBuilder();
        Logradouro logradouro = this.mapDeLogradouros.get(CEP);

        if (logradouro == null) {
            return "\nN�o existe logradouro com esse CEP!\n";
        }

        string.append("\nCEP do logradouro: ").append(logradouro.getCep()).append("\n")
                .append("Nome do logradouro: ").append(logradouro.getNomeLogradouro())
                .append("\n").append("Bairro do logradouro: ").append(logradouro.getBairro())
                .append("\n").append("Cidade do logradouro: ").append(logradouro.getCidade())
                .append("\n").append("Estado do logradouro: ").append(logradouro.getEstado())
                .append("\n");

        return string.toString();
    }

    public String pegarLogradouroPeloNome(String nomeLogradouro) {
        StringBuilder string = new StringBuilder();
        string.append("\n-----------------------------------------------");

        boolean entrou = false;
        for (Logradouro logradouro : this.mapDeLogradouros.values()) {
            if (nomeLogradouro.compareToIgnoreCase(logradouro.getNomeLogradouro()) == 0) {
                entrou = true;
                string.append("\nCEP do logradouro: ").append(logradouro.getCep()).append("\n")
                        .append("Nome do logradouro: ").append(logradouro.getNomeLogradouro())
                        .append("\n").append("Bairro do logradouro: ").append(logradouro.getBairro())
                        .append("\n").append("Cidade do logradouro: ").append(logradouro.getCidade())
                        .append("\n").append("Estado do logradouro: ").append(logradouro.getEstado())
                        .append("\n").append("-----------------------------------------------");
            }
        }
        if (entrou == false) {
            return "\nN�o existe logradouro com esse nome!\n";
        }
        string.append("\n");
        return string.toString();
    }

    public String retornarUFs() {
        StringBuilder string = new StringBuilder();
        string.append("\n");
        for (Estado estado : this.mapDeEstados.values()) {
            string.append(estado.getSiglaEstado()).append(" - ")
                    .append(estado.getNome()).append("\n");
        }
        return string.toString();
    }

    public String lerArquivos() {
        try {
            // Leitura do arquivo
            leitorUF.lerUF(this);
            leitorCidade.lerCidade(this);
            leitorBairros.lerBairro(this);
            leitorLogradouros.lerLogradouro();
        } catch (IOException ex) {
            return "\nErro na leitura!";
        }
        return "\nLeitura realizada com sucesso!";
    }

    // Realiza 3 vezes dataSets leitura do arquivo. Ao final do m�todo, � retornada dataSets m�dia do tempo de execu��o da leitura
    public String testeLeituraArquivo() {
        StringBuilder string = new StringBuilder();
        long[] vetorDeTempos = new long[3];
        string.append("\nTempo de teste = ");
        long tempoInicio;
        long tempoFim;
        long tempoTotal = 0;
        for (int i = 0; i < 3; i++) {
            tempoInicio = System.nanoTime();
            this.lerArquivos();
            tempoFim = System.nanoTime();
            tempoTotal += (tempoFim - tempoInicio);
            vetorDeTempos[i] = (tempoFim - tempoInicio);
        }
        string.append(tempoTotal / 3).append(" nanosegundos");
        //Gerando grafico e plotando o mesmo
        DefaultCategoryDataset dataSets = new DefaultCategoryDataset();
        for (int i = 0; i < vetorDeTempos.length; i++) {
            dataSets.setValue(vetorDeTempos[i], Integer.toString(i + 1), "");
        }
        JFreeChart graficoDeBarra3d = ChartFactory.createBarChart3D("Tempos de leituras", "Execucoes", "Tempo em nanosegundos", dataSets, PlotOrientation.VERTICAL, true,
                false, false);
        ChartFrame plot3d = new ChartFrame("Grafico de leitura dos arquivos", graficoDeBarra3d, true);
        plot3d.setVisible(true);
        plot3d.setSize(700, 800);
        return string.toString();
    }

    // Realiza 7 vezes uma busca por nome e sigla de cada estado e armazena em uma string. Ao final do m�todo, � retornada dataSets m�dia do tempo de execu��o da busca
    public String testeBuscaEstados() {
        StringBuilder string = new StringBuilder();
        string.append("\nTempo de teste = ");
        long[] vetorDeTempos = new long[7];
        long tempoInicio;
        long tempoFim;
        long tempoTotal = 0;
        for (int i = 0; i < 7; i++) {
            tempoInicio = System.nanoTime();
            this.retornarUFs();
            tempoFim = System.nanoTime();
            tempoTotal += (tempoFim - tempoInicio);
            vetorDeTempos[i] = (tempoFim - tempoInicio);
        }
        string.append(tempoTotal / 7).append(" nanosegundos");
        DefaultCategoryDataset dataSets = new DefaultCategoryDataset();
        for (int i = 0; i < vetorDeTempos.length; i++) {
            dataSets.setValue(vetorDeTempos[i], Integer.toString(i+1), "");
        }
        JFreeChart graficoDeBarra3d = ChartFactory.createBarChart3D("Tempos de busca dos estados", "Execucoes", "Tempo em nanosegundos", dataSets, PlotOrientation.VERTICAL, true,
                false, false);
        ChartFrame plot3d = new ChartFrame("Grafico de busca dos estados", graficoDeBarra3d, true);
        plot3d.setVisible(true);
        plot3d.setSize(700, 800);
        return string.toString();
    }

    // Realiza 7 vezes uma busca por nome de cada cidade de um estado e armazena em uma string. Ao final do m�todo, � retornada dataSets m�dia do tempo de execu��o da busca
    public String testeBuscaCidadesEstado(String sigla) {
        StringBuilder string = new StringBuilder();
        long[] vetorDeTempos = new long[7];
        string.append("\nTempo de teste = ");
        long tempoInicio;
        long tempoFim;
        long tempoTotal = 0;
        for (int i = 0; i < 7; i++) {
            tempoInicio = System.nanoTime();
            this.buscarCidadesDaUF(sigla);
            tempoFim = System.nanoTime();
            tempoTotal += (tempoFim - tempoInicio);
            vetorDeTempos[i] = (tempoFim - tempoInicio);
        }
        string.append(tempoTotal / 7).append(" nanosegundos");
        DefaultCategoryDataset dataSets = new DefaultCategoryDataset();
        for (int i = 0; i < vetorDeTempos.length; i++) {
            dataSets.setValue(vetorDeTempos[i], Integer.toString(i+1), "");
        }
        JFreeChart graficoDeBarra3d = ChartFactory.createBarChart3D("Tempos de busca das cidades do estado com sigla " + sigla, "Execucoes", "Tempo em nanosegundos", dataSets, PlotOrientation.VERTICAL, true,
                false, false);
        ChartFrame plot3d = new ChartFrame("Grafico de busca das cidades do estado com sigla " + sigla, graficoDeBarra3d, true);
        plot3d.setVisible(true);
        plot3d.setSize(700, 800);
        return string.toString();
    }

    public String testeBuscaBairrosCidade(String UF, String nomeCidade) {
        StringBuilder string = new StringBuilder();
        long[] vetorDeTempos = new long[7];
        string.append("\nTempo de teste = ");
        long tempoInicio;
        long tempoFim;
        long tempoTotal = 0;
        for (int i = 0; i < 7; i++) {
            tempoInicio = System.nanoTime();
            this.buscarBairrosDaCidade(UF, nomeCidade);
            tempoFim = System.nanoTime();
            tempoTotal += (tempoFim - tempoInicio);
            vetorDeTempos[i] = (tempoFim - tempoInicio);
        }
        string.append(tempoTotal / 7).append(" nanosegundos");
        DefaultCategoryDataset dataSets = new DefaultCategoryDataset();
        for (int i = 0; i < vetorDeTempos.length; i++) {
            dataSets.setValue(vetorDeTempos[i], Integer.toString(i+1), "");
        }
        JFreeChart graficoDeBarra3d = ChartFactory.createBarChart3D("Tempos de busca dos bairros da cidade " + nomeCidade.toUpperCase()
                + "-" + UF.toUpperCase(), "Execucoes", "Tempo em nanosegundos", dataSets, PlotOrientation.VERTICAL, true,
                false, false);
        ChartFrame plot3d = new ChartFrame("Grafico de busca dos bairros da cidade " + nomeCidade.toUpperCase()
                + "-" + UF.toUpperCase(), graficoDeBarra3d, true);
        plot3d.setVisible(true);
        plot3d.setSize(700, 800);
        return string.toString();
    }

    public String testeLogradourosBairro(String UF, String nomeCidade, String nomeBairro) {
        StringBuilder string = new StringBuilder();
        long[] vetorDeTempos = new long[7];
        string.append("\nTempo de teste = ");
        long tempoInicio;
        long tempoFim;
        long tempoTotal = 0;
        for (int i = 0; i < 7; i++) {
            tempoInicio = System.nanoTime();
            this.buscarLogradourosDoBairro(UF, nomeCidade, nomeBairro);
            tempoFim = System.nanoTime();
            tempoTotal += (tempoFim - tempoInicio);
            vetorDeTempos[i] = (tempoFim - tempoInicio);
        }
        string.append(tempoTotal / 7).append(" nanosegundos");
        DefaultCategoryDataset dataSets = new DefaultCategoryDataset();
        for (int i = 0; i < vetorDeTempos.length; i++) {
            dataSets.setValue(vetorDeTempos[i], Integer.toString(i+1), "");
        }
        JFreeChart graficoDeBarra3d = ChartFactory.createBarChart3D("Tempo de busca dos logradouros no bairro "
                + nomeBairro.toUpperCase() + " na cidade " + 
                nomeCidade.toUpperCase() + "-" + UF.toUpperCase(), "Execucoes", "Tempo em nanosegundos", dataSets, PlotOrientation.VERTICAL, true,
                false, false);
        ChartFrame plot3d = new ChartFrame("Grafico de busca dos logradouros no bairro "
                + nomeBairro.toUpperCase() + " na cidade " + 
                nomeCidade.toUpperCase() + "-" + UF.toUpperCase(), graficoDeBarra3d, true);
        plot3d.setVisible(true);
        plot3d.setSize(700, 800);
        return string.toString();
    }

    public String testeBuscaLogradouroCEP(String CEP) {
        StringBuilder string = new StringBuilder();
        long[] vetorDeTempos = new long[7];
        string.append("\nTempo de teste = ");
        long tempoInicio;
        long tempoFim;
        long tempoTotal = 0;
        for (int i = 0; i < 7; i++) {
            tempoInicio = System.nanoTime();
            this.pegarLogradouroPeloCEP(CEP);
            tempoFim = System.nanoTime();
            tempoTotal += (tempoFim - tempoInicio);
            vetorDeTempos[i] = (tempoFim - tempoInicio);
        }
        string.append(tempoTotal / 7).append(" nanosegundos");
        DefaultCategoryDataset dataSets = new DefaultCategoryDataset();
        for (int i = 0; i < vetorDeTempos.length; i++) {
            dataSets.setValue(vetorDeTempos[i], Integer.toString(i+1), "");
        }
        JFreeChart graficoDeBarra3d = ChartFactory.createBarChart3D("Tempo de busca dos logradouros pelo CEP "  + "'" + CEP + "'", "Execucoes", "Tempo em nanosegundos", dataSets, PlotOrientation.VERTICAL, true,
                false, false);
        ChartFrame plot3d = new ChartFrame("Grafico de busca do logradouro de CEP " + "'" + CEP + "'", graficoDeBarra3d, true);
        plot3d.setVisible(true);
        plot3d.setSize(700, 800);
        return string.toString();
    }

    public String testeBuscaLogradouroNome(String nome) {
        StringBuilder string = new StringBuilder();
        long[] vetorDeTempos = new long[7];
        string.append("\nTempo de teste = ");
        long tempoInicio;
        long tempoFim;
        long tempoTotal = 0;
        for (int i = 0; i < 7; i++) {
            tempoInicio = System.nanoTime();
            this.pegarLogradouroPeloNome(nome);
            tempoFim = System.nanoTime();
            tempoTotal += (tempoFim - tempoInicio);
            vetorDeTempos[i] = (tempoFim - tempoInicio);
        }
        string.append(tempoTotal / 7).append(" nanosegundos");
        DefaultCategoryDataset dataSets = new DefaultCategoryDataset();
        for (int i = 0; i < vetorDeTempos.length; i++) {
            dataSets.setValue(vetorDeTempos[i], Integer.toString(i+1), "");
        }
        JFreeChart graficoDeBarra3d = ChartFactory.createBarChart3D("Tempo de busca dos logradouros pelo nome " + "'" + nome.toUpperCase() + "'", "Execucoes", "Tempo em nanosegundos", dataSets, PlotOrientation.VERTICAL, true,
                false, false);
        ChartFrame plot3d = new ChartFrame("Grafico de busca dos logradouros de nome " + "'" + nome.toUpperCase() + "'", graficoDeBarra3d, true);
        plot3d.setVisible(true);
        plot3d.setSize(700, 800);
        return string.toString();
    }

}
