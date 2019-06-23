package taka521.grpc.server

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import taka521.grpc.HelloWorld
import taka521.grpc.HelloWorldServiceGrpc
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

class HelloWorldServer {

    private lateinit var server: Server

    internal class HelloWorldService : HelloWorldServiceGrpc.HelloWorldServiceImplBase() {

        override fun helloWorld(
            request: HelloWorld.HelloRequest?,
            responseObserver: StreamObserver<HelloWorld.HelloResponse>?
        ) {
            logger.info(request?.toString())
            super.helloWorld(request, responseObserver)
            responseObserver?.onNext(createRespose(request))
            responseObserver?.onCompleted()
        }

        private fun createRespose(request: HelloWorld.HelloRequest?) =
            HelloWorld.HelloResponse.newBuilder().setMessage("Hello World.").build()

    }

    private fun start() {
        val port = 8080
        server = ServerBuilder.forPort(port).addService(HelloWorldService()).build().start()
        logger.log(Level.INFO, "hello world service started, listening on {0} port.", port)

        Runtime.getRuntime().addShutdownHook(object: Thread() {
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

