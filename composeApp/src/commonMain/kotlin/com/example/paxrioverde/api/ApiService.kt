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
        valorCartao: String? = null,
        valorTotal: String? = null
    ): BoletoResponse {
        return client.post("boleto_app") {
            setBody(FormDataContent(Parameters.build {
                append("idcontrato", idcontrato.toString())
                append("idconvenio", idconvenio.toString())
                append("idmensalidade", idmensalidade.toString())
                append("id_mensalidade", idmensalidade.toString())
                
                if (!valorCartao.isNullOrEmpty() && valorCartao != "0,00" && valorCartao != "0.00") {
                    val formattedValor = valorCartao.replace(",", ".")
                    append("valor_cartao", formattedValor)
                    append("valor_cartao_adicional", formattedValor)
                    append("add_valor", formattedValor)
                    append("VALOR_CARTAO", formattedValor)
                }

                if (!valorTotal.isNullOrEmpty()) {
                    append("valor", valorTotal.replace(",", "."))
                    append("VALOR", valorTotal.replace(",", "."))
                }
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

    suspend fun gerarCartao(
        idcliente: Int,
        tipo: String,
        nomeDependente: String,
        idcontrato: Int,
        idconvenio: Int,
        valor: String,
        idmensalidade: Int,
        dtvencimento: String,
        idfilial: Int,
        idcaixa: Int
    ): GerarCartaoResponse {
        return client.post("gerar_cartao_app") {
            setBody(FormDataContent(Parameters.build {
                // Identificação básica
                append("idcliente", idcliente.toString())
                append("id_cliente", idcliente.toString())
                append("ID_CLIENTE", idcliente.toString())
                
                append("idcontrato", idcontrato.toString())
                append("id_contrato", idcontrato.toString())
                append("ID_CONTRATO", idcontrato.toString())
                
                append("idconvenio", idconvenio.toString())
                append("id_convenio", idconvenio.toString())
                append("ID_CONVENIO", idconvenio.toString())
                
                append("tipo", tipo)
                append("nomedependente", nomeDependente)
                append("nome_dependente", nomeDependente)

                // Parâmetros de Routing Financeiro
                append("idcaixa", idcaixa.toString())
                append("id_caixa", idcaixa.toString())
                append("IDCAIXA", idcaixa.toString())
                append("ID_CAIXA", idcaixa.toString())
                
                append("idfilial", idfilial.toString())
                append("id_filial", idfilial.toString())
                append("IDFILIAL", idfilial.toString())
                append("ID_FILIAL", idfilial.toString())
                
                append("dtvencimento", dtvencimento)
                append("data_vencimento", dtvencimento)
                append("dt_vencimento", dtvencimento)
                append("DATA_VENCIMENTO", dtvencimento)
                
                // Parâmetros financeiros e IDs de mensalidade
                append("idmensalidade", idmensalidade.toString())
                append("id_mensalidade", idmensalidade.toString())
                append("IDMENSALIDADE", idmensalidade.toString())
                append("ID_MENSALIDADE", idmensalidade.toString())

                val valorLimpo = valor.replace(",", ".")
                append("valor", valorLimpo)
                append("VALOR", valorLimpo)
                append("valor_cartao", valorLimpo)
                append("VALOR_CARTAO", valorLimpo)
                append("valor_cartao_adicional", valorLimpo)

                // Geração de Cobrança Formal (Conforme Backend)
                append("gerar_financeiro", "SIM")
                append("GERAR_FINANCEIRO", "SIM")
                append("lancar_proxima", "S")
                append("LANCAR_PROXIMA", "S")
                append("lancar_proxima_mensalidade", "S")
                append("ADC_MENSALIDADE", "1")
                append("adc_mensalidade", "1")
                append("ADD_MENSALIDADE", "1")
                append("taxa_adesao", "S")
                append("TAXA_ADESAO", "S")
                append("gerar_boleto", "S")
                append("GERAR_BOLETO", "S")
                append("SITUACAO", "PENDENTE")
                append("situacao", "PENDENTE")
                append("gerar_debito_adicional", "S")
                append("GERAR_DEBITO_ADICIONAL", "S")
                
                // Vincular a Mensalidades
                append("inserir_mensalidade", "S")
                append("INSERIR_MENSALIDADE", "S")
                append("id_conta_corrente", "S")
                append("vincular_financeiro", "S")
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
