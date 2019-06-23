package taka521.grpc.client

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import taka521.grpc.helloworld.HelloRequest
import taka521.grpc.helloworld.HelloWorldServiceGrpc
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

class HelloWorldClient {

    private var channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build()

    fun call() {
        val blockingStub = HelloWorldServiceGrpc.newBlockingStub(channel)

        val request = HelloRequest.newBuilder().apply {
            id = 1L
            user = HelloRequest.User.newBuilder().apply {
                name = "Bob"
                age = 20
                gender = HelloRequest.GENDER.MAN
            }.build()
            option = "option"
        }.build()

        logger.info(
            "id = ${request.id}, " +
                    "user = [name = ${request.user.name}, age = ${request.user.age}, gender = ${request.user.gender}]. " +
                    "option = ${request.option}"
        )

        val response = try {
            blockingStub.helloWorld(request)
        } catch (e: StatusRuntimeException) {
            logger.log(Level.WARNING, "failed: {0}", e.status)
            return
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        }

        logger.info("Hello World Service: ${response.message}")
    }

    companion object {

        val logger: Logger = Logger.getLogger(HelloWorldClient::class.java.name)

        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val client = HelloWorldClient()
            client.call()
        }
    }
}