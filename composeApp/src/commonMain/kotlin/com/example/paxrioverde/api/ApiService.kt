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

object ApiService {
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
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120000
            connectTimeoutMillis = 120000
            socketTimeoutMillis = 120000
        }
        defaultRequest {
            url(AppConstants.BASE_URL) 
            header(HttpHeaders.Accept, "*/*")
            header(HttpHeaders.Connection, "keep-alive")
            header(HttpHeaders.UserAgent, "PostmanRuntime/7.32.3")
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
        idmensalidade: Int
    ): PixResponse {
        // Usando exatamente o padrão que você enviou:
        // Endpoint: gerar_pix_cola
        // Método: GET (com parâmetros na URL)
        return client.get("gerar_pix_cola") {
            url {
                parameters.append("PIX_REGISTRADO", "SIM")
                parameters.append("IDCAIXA", idcaixa.toString())
                parameters.append("IDCONTRATO", idcontrato.toString())
                parameters.append("IDCONVENIO", idconvenio.toString())
                parameters.append("DATA_VENCIMENTO", dtvencimento)
                parameters.append("IDMENSALIDADE", idmensalidade.toString())
            }
        }.body()
    }

    suspend fun getBoleto(idcontrato: Int, idconvenio: Int, idmensalidade: Int): BoletoResponse {
        return client.post("boleto_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcontrato", idcontrato.toString())
                append("idconvenio", idconvenio.toString())
                append("idmensalidade", idmensalidade.toString())
            }))
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

    suspend fun gerarCartao(idcliente: Int, tipo: String, nomeDependente: String): GerarCartaoResponse {
        return client.post("gerar_cartao_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcliente", idcliente.toString())
                append("tipo", tipo)
                append("nomedependente", nomeDependente)
            }))
        }.body()
    }

    suspend fun listaPets(idcliente: Int): PetsResponse {
        return client.post("lista_pets_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcliente", idcliente.toString())
            }))
        }.body()
    }

    suspend fun inserirPet(
        idcontrato: Int,
        idconvenio: Int,
        nome: String,
        raca: String,
        dtnascimento: String,
        foto: String,
        situacao: String,
        idpet: Int
    ): PetActionResponse {
        return client.post("inserir_pet_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcontrato", idcontrato.toString())
                append("idconvenio", idconvenio.toString())
                append("nome", nome)
                append("raca", raca)
                append("dtnascimento", dtnascimento)
                append("foto", foto)
                append("situacao", situacao)
                append("idpet", idpet.toString())
            }))
        }.body()
    }
}
