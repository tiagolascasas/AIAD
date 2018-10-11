# SmartSemaphores - Controlo de Semáforos em Cruzamentos

## AIAD 2018/2019

 <p align="justify"> O objetivo deste tema é modelar, usando um sistema multi-agente, o comportamento dos semáforos num conjunto de cruzamentos conectados entre si. Cada agente será um controlo semafórico para cada semáforo na rede de estradas, sendo que se pretende minimizar o tempo em que um carro tem de esperar num semáforo e o tempo que um conjunto de pedestres tem de esperar para poder atravessar uma estrada com semáforo. Os semáforos deverão negociar entre si quando é que devem mandar prosseguir ou parar o tráfego com base no fluxo atual de veículos em cada via e do número de pedestres a querer atravessar. Haverá também a possibilidade de adaptar o tráfego de modo a agilizar a viagem de veículos de emergência, que devem ter de parar o mínimo de tempo possível em cruzamentos. </p>
 <p align="justify"> Para tais objetivos, cada agente deverá poder receber, via sensores, o valor do fluxo de veículos na(s) via(s) que lhe dizem respeito, assim como informação sobre se existem pedestres a querer ou não atravessar. Outros sensores darão informações sobre a existência de veículos de emergência nas vias. Os agentes deverão negociar o tempo de passagem entre si de modo a não criar conflitos entre as vias e de modo a otimizar as métricas acima descritas. Um semáforo com um maior número de veículos parados ou com um maior número de pedestres a querer atravessar terá maior prioridade para passar para verde, enquanto que um semáforo que esteja verde terá uma maior probabilidade de passar para para vermelho se tiver com pouco fluxo ou com um tempo excessivo em verde. Os agentes comunicarão entre si declarando as suas intenções de passar para outro estado, e tentarão negociar entre si quais as intenções que se devem de facto realizar de modo a otimizar as métricas. Haverá também uma geração dinâmica de informação sensorial, pretendendo simular situações com números variados e aleatórios de fluxo de veículos, pedestres e veículos de emergência. A performance do SMA será medida comparando com um modelo em que todos os semáforos mudam de estado com base em tempos pré-alocados, ou seja, num modelo que não toma em conta o estado do ambiente em que se encontra.  </p>
 
 ***

| Exemplos de variáveis dependentes  | Exemplos de variáveis independentes |
| ------------- | ------------- |
| Tempo que cada semáforo permanece num certo estado (verde/vermelho)  | Fluxo de veículos em cada via da rede |
| Tempo que um conjunto de pedestres espera antes de atravessar  | Número de pedestres em cada cruzamento  |
| Tempo que um veículo gasta parado em semáforos  | Taxa de aparição veículos de emergência na rede |





