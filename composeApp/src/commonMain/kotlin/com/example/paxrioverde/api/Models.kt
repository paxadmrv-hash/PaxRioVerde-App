package com.example.paxrioverde.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
    @SerialName("idfilial") val idfilial: Int? = null,
    @SerialName("idcliente") val idcliente: Int? = null,
    @SerialName("nomecliente") val nomecliente: String? = null,
    @SerialName("plano") val plano: String? = null,
    @SerialName("prox_mens") val prox_mens: String? = null,
    @SerialName("valormensalidade") val valormensalidade: String? = null,
    @SerialName("idcaixa_pix") val idcaixa_pix: Int? = null,
    @SerialName("valorcartao") val valorcartao: String? = null,
    @SerialName("idcontrato_prox_mens") val idcontrato_prox_mens: Int? = null,
    @SerialName("idconvenio_prox_mens") val idconvenio_prox_mens: Int? = null,
    @SerialName("idmensalidade_prox_mens") val idmensalidade_prox_mens: Int? = null,
    @SerialName("valormens_prox_mens") val valormens_prox_mens: String? = null,
    @SerialName("boleto_prox_mens") val boleto_prox_mens: Boolean? = false,
    @SerialName("cpf") val cpf: String? = null
)

@Serializable
data class DependentesResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("dependentes") val dependentes: List<DependenteItem>? = emptyList()
)

@Serializable
data class DependenteItem(
    @SerialName("nome_dependente") private val n1: String? = null,
    @SerialName("nomedependente") private val n2: String? = null,
    @SerialName("nomeDependente") private val n3: String? = null,
    @SerialName("NOME") private val n4: String? = null,
    @SerialName("parentesco") val parentesco: String? = null,
    @SerialName("cpf") val cpf: String? = null
) {
    val nomeDependente: String? get() = n1 ?: n2 ?: n3 ?: n4
}

@Serializable
data class MensalidadesResponse(
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
    @SerialName("anos") val anos: List<AnoItem>? = null
)

@Serializable
data class AnoItem(
    @SerialName("ano") val ano: Int,
    @SerialName("mensalidades") val mensalidades: List<MensalidadeItem>
)

@Serializable
data class MensalidadeItem(
    @SerialName("idcontrato") val idcontrato: Int,
    @SerialName("idconvenio") val idconvenio: Int,
    @SerialName("idmensalidade") val idmensalidade: Int,
    @SerialName("dtvencimento") val dtvencimento: String,
    @SerialName("dtpagamento") val dtpagamento: String,
    @SerialName("valormensalidade") val valormensalidade: String,
    @SerialName("valordesconto") val valordesconto: String,
    @SerialName("valorpago") val valorpago: String,
    @SerialName("pago") val pago: Boolean,
    @SerialName("boleto") val boleto: Boolean? = false
)

@Serializable
data class PixResponse(
    @SerialName("PIX") val pixCode: String
)

@Serializable
data class BoletoResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("idcontrato") val idContrato: Int? = null,
    @SerialName("mesano") val mesAno: String? = null,
    @SerialName("cpf") val cpf: String? = null,
    @SerialName("CAMINHO_ARQUIVO") val caminhoArquivo: String? = null,
    @SerialName("CODIGO_BARRA") val codigoBarra: String? = null,
    @SerialName("QRCODE_PIX") val qrcodePix: String? = null,
    @SerialName("PDF_BOLETO") val pdfBoletoBase64: String? = null
)

@Serializable
data class CartoesResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("cartoes") val cartoes: List<CartaoItem>? = null
)

@Serializable
data class CartaoItem(
    @SerialName("idControle") val idControle: Int,
    @SerialName("valor") val valor: String? = null,
    @SerialName("idContrato") val idContrato: String? = null,
    @SerialName("idConvenio") val idConvenio: String? = null,
    @SerialName("dtEmissao") val dtEmissao: String? = null,
    @SerialName("dtValidade") val dtValidade: String,
    @SerialName("nomeDependente") val nomeDependente: String? = null,
    @SerialName("nomeCliente") val nomeCliente: String,
    @SerialName("dep") val dep: String,
    @SerialName("tipo") val tipo: String,
    @SerialName("pdf_base64") val pdfBase64: String? = null
)

@Serializable
data class CartaoImagemResponse(
    @SerialName("CARTAO") val imagemBase64: String
)

@Serializable
data class GerarCartaoResponse(
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String
)

@Serializable
data class PetsResponse(
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
    @SerialName("pets") val pets: List<PetItem>? = emptyList()
)

@Serializable
data class PetItem(
    @SerialName("idcontrato") val idcontrato: String? = null,
    @SerialName("idconvenio") val idconvenio: String? = null,
    @SerialName("idpet") val idpet: String? = null,
    @SerialName("nome") val nome: String? = null,
    @SerialName("raca") val raca: String? = null,
    @SerialName("dtnascimento") val dtnascimento: String? = null,
    @SerialName("foto") val foto: String? = null,
    @SerialName("situacao") val situacao: String? = null
)

@Serializable
data class PetActionResponse(
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String
)
