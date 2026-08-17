package com.example.paxrioverde.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.example.paxrioverde.util.AppConstants
import com.example.paxrioverde.util.SessionManager
import com.example.paxrioverde.util.isDebug

class ApiService(private val sessionManager: SessionManager) {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = if (isDebug) LogLevel.ALL else LogLevel.INFO
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }
        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response ->
                !response.status.isSuccess() && response.status.value in 500..599
            }
            retryOnExceptionIf { _, cause ->
                cause is kotlinx.io.IOException || cause is io.ktor.client.network.sockets.SocketTimeoutException
            }
            exponentialDelay()
        }
        defaultRequest {
            url(AppConstants.BASE_URL)
            header(HttpHeaders.Accept, "*/*")
            header(HttpHeaders.Connection, "keep-alive")
            header(HttpHeaders.UserAgent, "PostmanRuntime/7.32.3")
            
            // Adiciona Token se disponível
            sessionManager.getAccessToken()?.let { token ->
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    suspend fun login(login: String, senha: String): LoginResponse {
        return client.post("login_app") {
            setBody(FormDataContent(Parameters.build {
                append("login", login)
                append("senha", senha)
            }))
        }.body()
    }

    suspend fun registrar(cpf: String, celular: String, email: String, senha: String): LoginResponse {
        return client.post("criar_usuario_app") {
            setBody(FormDataContent(Parameters.build {
                append("cpf", cpf)
                append("celular", celular)
                append("email", email)
                append("senha", senha)
            }))
        }.body()
    }

    suspend fun getDependentes(idcliente: Int): DependentesResponse {
        return client.post("dependentes_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcliente", idcliente.toString())
            }))
        }.body()
    }

    suspend fun getMensalidades(idcliente: Int, qtdeanos: Int = 4): MensalidadesResponse {
        return client.post("mens_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcliente", idcliente.toString())
                append("qtdeanos", qtdeanos.toString())
            }))
        }.body()
    }

    suspend fun gerarPix(
        idcaixa: Int,
        idcontrato: Int,
        idconvenio: Int,
        dtvencimento: String,
        idmensalidade: Int,
        valorCartao: String? = null,
        valorTotal: String? = null
    ): PixResponse {
        return client.post("gerar_pix_cola") {
            setBody(FormDataContent(Parameters.build {
                append("PIX_REGISTRADO", "SIM")
                append("IDCAIXA", idcaixa.toString())
                append("IDCONTRATO", idcontrato.toString())
                append("IDCONVENIO", idconvenio.toString())
                append("DATA_VENCIMENTO", dtvencimento)
                append("IDMENSALIDADE", idmensalidade.toString())
                append("ID_MENSALIDADE", idmensalidade.toString())
                
                if (!valorCartao.isNullOrEmpty() && valorCartao != "0,00" && valorCartao != "0.00") {
                    val formattedValor = valorCartao.replace(",", ".")
                    append("VALOR_CARTAO", formattedValor)
                    append("VALOR_CARTAO_ADICIONAL", formattedValor)
                    append("ADD_VALOR", formattedValor)
                    append("valor_cartao", formattedValor)
                }

                if (!valorTotal.isNullOrEmpty()) {
                    append("VALOR", valorTotal.replace(",", "."))
                    append("valor", valorTotal.replace(",", "."))
                }
            }))
        }.body()
    }

    suspend fun getBoleto(
        idcontrato: Int,
        idconvenio: Int,
        idmensalidade: Int,
        cpf: String,
        mesano: String,
        valorCartao: String? = null,
        valorTotal: String? = null
    ): BoletoResponse {
        return client.get("boleto") {
            url {
                parameters.append("IDCONTRATO", idcontrato.toString())
                parameters.append("CPF", cpf)
                parameters.append("MESANO", mesano)
                parameters.append("idcontrato", idcontrato.toString())
                parameters.append("idconvenio", idconvenio.toString())
                parameters.append("idmensalidade", idmensalidade.toString())
                parameters.append("id_mensalidade", idmensalidade.toString())

                if (!valorCartao.isNullOrEmpty() && valorCartao != "0,00" && valorCartao != "0.00") {
                    val formattedValor = valorCartao.replace(",", ".")
                    parameters.append("valor_cartao", formattedValor)
                    parameters.append("valor_cartao_adicional", formattedValor)
                    parameters.append("add_valor", formattedValor)
                    parameters.append("VALOR_CARTAO", formattedValor)
                }

                if (!valorTotal.isNullOrEmpty()) {
                    parameters.append("valor", valorTotal.replace(",", "."))
                    parameters.append("VALOR", valorTotal.replace(",", "."))
                }
            }
        }.body()
    }

    suspend fun getCartoes(idcliente: Int): CartoesResponse {
        return client.post("lista_cartoes_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcliente", idcliente.toString())
            }))
        }.body()
    }

    suspend fun getImagemCartao(idControle: Int): CartaoImagemResponse {
        return client.get("cartaorioverde") {
            url {
                parameters.append("idcontrole", idControle.toString())
            }
        }.body()
    }

    suspend fun gerarCartao(
        idcaixa: Int,
        idcliente: Int,
        tipo: String,
        nomeDependente: String?,
        gratuito: String,
        idcontrato: Int = 0,
        idconvenio: Int = 0,
        cpfDependente: String? = null,
        dtvencimento: String? = null,
        parentesco: String? = null,
        idfilial: Int = 0
    ): GerarCartaoResponse {
        return client.post("gerar_cartao_app") {
            url {
                parameters.clear()
                // Parâmetros Base
                parameters.set("idcaixa", idcaixa.toString())
                parameters.set("id_caixa", idcaixa.toString())
                parameters.set("idcliente", idcliente.toString())
                parameters.set("id_cliente", idcliente.toString())
                
                // Parâmetros de Tipo e Dependência
                parameters.set("tipo", tipo)
                parameters.set("dep", if (tipo.lowercase().contains("titular")) "N" else "S")
                parameters.set("parentesco", parentesco ?: "")
                
                // Nome do Dependente (Múltiplos formatos para garantir match)
                val nome = nomeDependente ?: ""
                parameters.set("nomedependente", nome)
                parameters.set("nome_dependente", nome)
                parameters.set("nome", nome)
                
                // Regra de Gratuidade (Conforme orientação do backend)
                parameters.set("gratuito", gratuito)
                
                // Vínculos de Plano (Essencial para localizar a vaga do dependente)
                if (idcontrato != 0) parameters.set("idcontrato", idcontrato.toString())
                if (idconvenio != 0) parameters.set("idconvenio", idconvenio.toString())
                if (idfilial != 0) parameters.set("idfilial", idfilial.toString())
                
                // Metadados
                parameters.set("dtvencimento", dtvencimento ?: "")
                parameters.set("cpf", cpfDependente ?: "")
                parameters.set("is_virtual", "S")
                parameters.set("cartao_virtual", "S")
                parameters.set("situacao", "ATIVO")
            }
        }.body()
    }

    suspend fun gerarCartaoPix(
        idcaixa: Int,
        idcliente: Int,
        tipo: String,
        nomeDependente: String
    ): GerarCartaoPixResponse {
        return client.post("gerar_cartao_pix_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcaixa", idcaixa.toString())
                append("idcliente", idcliente.toString())
                append("tipo", tipo)
                append("nomedependente", nomeDependente)
            }))
        }.body()
    }

    suspend fun verificarPixPago(identificadorPix: String): VerificarPixPagoResponse {
        return client.get("verifica_pix_pago") {
            url {
                parameters.append("identificador_pix", identificadorPix)
            }
        }.body()
    }

    suspend fun listaPets(idcliente: Int): PetsResponse {
        return client.post("lista_pet") {
            url {
                parameters.append("idcliente", idcliente.toString())
            }
        }.body()
    }

    suspend fun inserirPet(
        idcliente: Int,
        idcontrato: Int,
        idconvenio: Int,
        idpet: Int,
        nome: String,
        raca: String,
        dtnascimento: String,
        foto: String,
        situacao: String
    ): PetActionResponse {
        return client.post("inserir_pet") {
            setBody(FormDataContent(Parameters.build {
                append("idcliente", idcliente.toString())
                append("idcontrato", idcontrato.toString())
                append("idconvenio", idconvenio.toString())
                append("idpet", idpet.toString())
                append("nome", nome)
                append("raca", raca)
                append("dtnascimento", dtnascimento)
                append("foto", foto)
                append("situacao", situacao)
            }))
        }.body()
    }

    suspend fun esquecerSenha(cpfOrEmail: String): GenericResponse {
        return client.post("esquecer_senha_app") {
            setBody(FormDataContent(Parameters.build {
                if (cpfOrEmail.contains("@")) {
                    append("email", cpfOrEmail)
                    append("login", cpfOrEmail)
                } else {
                    val digits = cpfOrEmail.filter { it.isDigit() }
                    append("cpf", digits)
                    append("login", digits)
                }
            }))
        }.body()
    }

    suspend fun redefinirSenha(cpfOrEmail: String, token: String, senha: String): GenericResponse {
        return client.post("redefinir_senha_app") {
            setBody(FormDataContent(Parameters.build {
                if (cpfOrEmail.contains("@")) {
                    append("email", cpfOrEmail)
                    append("login", cpfOrEmail)
                } else {
                    val digits = cpfOrEmail.filter { it.isDigit() }
                    append("cpf", digits)
                    append("login", digits)
                }
                append("token", token)
                append("senha", senha)
                append("password", senha)
                append("codigo", token)
            }))
        }.body()
    }

    suspend fun atuCpfDependente(idcliente: Int, cpf: String, nomedependente: String): GenericResponse {
        return client.post("atu_cpf_dependente") {
            setBody(FormDataContent(Parameters.build {
                append("idcliente", idcliente.toString())
                append("cpf", cpf)
                append("nomedependente", nomedependente)
            }))
        }.body()
    }
}
