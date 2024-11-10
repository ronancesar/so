import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//Ronan César Lourenço Soares
class Processo {
    int pid;
    int tempoTotal;
    int tempoProcessado = 0;
    int contadorPrograma = 1;
    String estado = "PRONTO";
    int numOperacoesES = 0;
    int numUsosCPU = 0;

    public Processo(int pid, int tempoTotal) {
        this.pid = pid;
        this.tempoTotal = tempoTotal;
    }

    public boolean isTerminado() {
        return tempoProcessado >= tempoTotal;
    }

    @Override
    public String toString() {
        return "PID: " + pid + ", TP: " + tempoProcessado + ", CP: " + contadorPrograma +
               ", EP: " + estado + ", NES: " + numOperacoesES + ", N_CPU: " + numUsosCPU;
    }
}

public class SimuladorSO {
    private static final int QUANTUM = 1000;
    private static final String ARQUIVO_PROCESSOS = "tabela_de_processos.txt";
    private static final Random random = new Random();

    public static void main(String[] args) {
        List<Processo> processos = inicializarProcessos();
        try {
            salvarDadosProcessos(processos);
            executarProcessos(processos);
            System.out.println("Simulação concluída.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<Processo> inicializarProcessos() {
        List<Processo> processos = new ArrayList<>();
        int[][] dadosProcessos = {
            {0, 10000}, {1, 5000}, {2, 7000}, {3, 3000}, {4, 3000},
            {5, 8000}, {6, 2000}, {7, 5000}, {8, 4000}, {9, 10000}
        };
        for (int[] dados : dadosProcessos) {
            processos.add(new Processo(dados[0], dados[1]));
        }
        return processos;
    }

    private static void salvarDadosProcessos(List<Processo> processos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_PROCESSOS))) {
            for (Processo processo : processos) {
                writer.write(processo.toString());
                writer.newLine();
            }
        }
    }

    private static void executarProcessos(List<Processo> processos) throws IOException, InterruptedException {
        while (processos.stream().anyMatch(p -> !p.isTerminado())) {
            for (Processo processo : processos) {
                if (processo.isTerminado()) continue;

                processo.estado = "EXECUTANDO";
                processo.numUsosCPU++;  
                processo.contadorPrograma = processo.tempoProcessado + 1;
                salvarDadosProcessos(processos);

                for (int ciclo = 0; ciclo < QUANTUM; ciclo++) {
                    if (processo.tempoProcessado >= processo.tempoTotal) break;

                    if (random.nextDouble() < 0.01) {
                        processo.estado = "BLOQUEADO";
                        processo.numOperacoesES++;
                        salvarDadosProcessos(processos);
                        System.out.println("PID " + processo.pid + " EXECUTANDO >>> BLOQUEADO");
                        break;
                    }

                    processo.tempoProcessado++;
                    processo.contadorPrograma = processo.tempoProcessado + 1;
                }

                if (processo.isTerminado()) {
                    processo.estado = "TERMINADO";
                    salvarDadosProcessos(processos);
                    System.out.println("PID " + processo.pid + " TERMINADO com dados: " + processo);
                } else if (processo.estado.equals("BLOQUEADO") && random.nextDouble() < 0.3) {
                    processo.estado = "PRONTO";
                    System.out.println("PID " + processo.pid + " BLOQUEADO >>> PRONTO");
                } else if (!processo.estado.equals("BLOQUEADO")) {
                    processo.estado = "PRONTO";
                    salvarDadosProcessos(processos);
                    System.out.println("PID " + processo.pid + " EXECUTANDO >>> PRONTO");
                }

                Thread.sleep(100);
            }
        }
    }
}
