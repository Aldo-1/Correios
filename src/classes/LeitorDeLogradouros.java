/**
 *
 * @author Aldo Wanderley Costa Junior
 */
package classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeitorDeLogradouros {
    
    // ATRIBUTOS
    private ArrayList<Runnable> runablesArquivosLogradouros;

    // CONSTRUTOR
    public LeitorDeLogradouros(File arquivo, BancoDeDados bancoDeDados) throws IOException {
        // Iniciando o vetor
        this.runablesArquivosLogradouros = new ArrayList<>();
        // Filtrando os arquivos
        this.filtrarArquivos(arquivo.listFiles(), bancoDeDados);
        // Checando se o diretorio e valido
        if (this.runablesArquivosLogradouros.isEmpty()) {
            throw new IOException("O Diretorio de Logradouros informado � invalido");
        }
    }
    
    // M�TODOS
    // Filtrando os arquivos so de LOGRADOUROS
    public void filtrarArquivos(File[] arquivosVetor, BancoDeDados bancoDeDados) throws FileNotFoundException {
        for (int i = 0; i < arquivosVetor.length; i++) {
            if (arquivosVetor[i].getName().contains("LOGRADOUROS.TXT")) {
                this.runablesArquivosLogradouros.add(new RunnableLeitorLogradouro(arquivosVetor[i], bancoDeDados));
            }
        }
    }
    // Lendo o logradouro
    public void lerLogradouro() {
        // Iniciando a pool de threads pra que cada uma seja executada e os logradouros sejam extraidos
        ExecutorService poolDeThreads = Executors.newFixedThreadPool(this.runablesArquivosLogradouros.size());
        for (int i = 0; i < this.runablesArquivosLogradouros.size(); i++) {
            poolDeThreads.execute(this.runablesArquivosLogradouros.get(i));
        }
        poolDeThreads.shutdown();
        
        while(!poolDeThreads.isTerminated()){
        }
    }
}
