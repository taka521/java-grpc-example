package taka521.grpc.server

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import taka521.grpc.helloworld.HelloRequest
import taka521.grpc.helloworld.HelloResponse
import taka521.grpc.helloworld.HelloWorldServiceGrpc
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Level
import java.util.logging.Logger

class HelloWorldServer {

    private lateinit var server: Server

    internal class HelloWorldService : HelloWorldServiceGrpc.HelloWorldServiceImplBase() {

        override fun helloWorld(
            request: HelloRequest?,
            responseObserver: StreamObserver<HelloResponse>?
        ) {
            logger.info(
                "id = ${request?.id}, " +
                        "user = [name = ${request?.user?.name}, age = ${request?.user?.age}, gender = ${request?.user?.gender}]. " +
                        "option = ${request?.option}"
            )
            responseObserver?.onNext(createResponse(request))
            responseObserver?.onCompleted()
        }

        private fun createResponse(request: HelloRequest?): HelloResponse? {
            logger.info(request.toString())
            return HelloResponse.newBuilder()
                .setMessage("Hello World.")
                .setOption(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .build()
        }

    }

    private fun start() {
        val port = 8080
        server = ServerBuilder.forPort(port).addService(HelloWorldService()).build().start()
        logger.log(Level.INFO, "hello world service started, listening on {0} port.", port)

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                System.err.println("hello world server is shutting down...")
                this@HelloWorldServer.stop()
                System.err.println("server shutdown.")
            }
        })
    }

    private fun stop() {
        server.shutdown()
    }

    @Throws(InterruptedException::class)
    private fun awaitTermination() = server.awaitTermination()

    companion object {

        /** Logger */
        val logger: Logger = Logger.getLogger(HelloWorldService::class.java.name)

        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val server = HelloWorldServer()
            server.start()
            server.awaitTermination()
        }
    }
}

