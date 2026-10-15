pipeline {
    agent any

    // Ferramentas globais configuradas no Jenkins (Gerenciar Jenkins -> Global Tool Configuration)
    tools {
        // Altere os nomes abaixo caso seu Jenkins utilize nomenclaturas diferentes (ex: 'JDK-17', 'Maven-3.9')
        maven 'Maven 3'
        jdk   'JDK 17'
    }

    environment {
        APP_NAME = 'clinica-veterinaria'
        JAR_NAME = 'clinica-veterinaria-0.0.1-SNAPSHOT.jar'
        IMAGE_NAME = 'zoomed/clinica-veterinaria'
    }

    options {
        // Mantém apenas as últimas 10 builds para economizar espaço em disco
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Ambiente & Informações') {
            steps {
                script {
                    echo "=========================================================="
                    echo " Iniciando Pipeline de CI/CD: ${env.APP_NAME}"
                    echo " Build: #${env.BUILD_NUMBER}"
                    echo " Branch: ${env.BRANCH_NAME ?: 'master/main'}"
                    echo " Data/Hora: ${new Date().format('dd/MM/yyyy HH:mm:ss')}"
                    echo "=========================================================="

                    // Diagnóstico do ambiente (funciona tanto em Linux quanto em Windows)
                    if (isUnix()) {
                        sh 'java -version'
                        sh 'mvn -version'
                    } else {
                        bat 'java -version'
                        bat 'mvn -version'
                    }
                }
            }
        }

        stage('Compilação (Build)') {
            steps {
                echo 'Compilando os fontes do projeto Spring Boot...'
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        stage('Testes Automatizados (JUnit 5)') {
            steps {
                echo 'Executando a suíte de testes unitários e de integração...'
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    // Publica os relatórios de testes XML do Surefire para exibição dos gráficos no Jenkins
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Empacotamento (Package)') {
            steps {
                echo 'Gerando o arquivo executável (.jar) da aplicação...'
                script {
                    // Pula os testes pois já foram validados com sucesso no estágio anterior
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
            post {
                success {
                    // Arquiva o artefato .jar gerado para download e histórico na interface do Jenkins
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Docker Build (Opcional)') {
            // Este estágio será executado apenas se o Docker estiver disponível no agente ou na branch principal
            when {
                expression { return fileExists('Dockerfile') }
            }
            steps {
                script {
                    echo 'Construindo imagem Docker da aplicação...'
                    try {
                        if (isUnix()) {
                            sh "docker build -t ${env.IMAGE_NAME}:${env.BUILD_NUMBER} -t ${env.IMAGE_NAME}:latest ."
                        } else {
                            bat "docker build -t ${env.IMAGE_NAME}:${env.BUILD_NUMBER} -t ${env.IMAGE_NAME}:latest ."
                        }
                        echo "Imagem Docker ${env.IMAGE_NAME}:${env.BUILD_NUMBER} criada com sucesso!"
                    } catch (Exception e) {
                        echo "Aviso: Docker não disponível ou daemon inativo no agente. Prosseguindo sem build da imagem. Erro: ${e.message}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finalizado para a build #${env.BUILD_NUMBER}."
        }
        success {
            echo '=========================================================='
            echo ' [SUCESSO] Pipeline de CI/CD finalizado com êxito!'
            echo " Artefato pronto em: target/${env.JAR_NAME}"
            echo '=========================================================='
        }
        failure {
            echo '=========================================================='
            echo ' [FALHA] Ocorreu um erro durante a execução do Pipeline!'
            echo ' Verifique o console log e os relatórios de teste.'
            echo '=========================================================='
        }
        unstable {
            echo '=========================================================='
            echo ' [INSTÁVEL] A build passou, mas houve testes com falha.'
            echo '=========================================================='
        }
    }
}
