<template>
  <div>
    <div id="chatBox" class="container">
      <div class="row h-100">
        <div id="conversationsList" class="col-3 p-3 border-right">
          <h5 class="black text-center">Minhas Conversas</h5>
          <hr />
          <ul class="list-group mb-3">
            <li
              v-for="conversation in conversations"
              :key="conversation.conversationId"
              class="list-group-item list-group-item-action mb-1"
              @click="loadMessages(conversation.conversationId)"
            >
              {{ conversation.name }}
            </li>
          </ul>
          <button class="btn btn-primary btn-block" @click="createNewConversation">Novo Chat</button>
        </div>

        <div id="chatContainer" class="col-9 p-3 d-flex flex-column justify-content-between">
          <div id="chatArea" ref="chatArea" class="border rounded p-3 mb-3" style="height: 550px; overflow-y: scroll">
            <div v-for="(message, index) in messages" :key="index" :class="['chat-message', message.role, 'my-2', 'p-2', 'rounded']">
              <span v-if="message.role === 'agent'">
                <font-awesome-icon icon="desktop" class="mr-2" />
                {{ message.content }}
              </span>
              <span v-else class="d-flex align-items-center justify-content-end">
                {{ message.content }}
                <font-awesome-icon icon="user-circle" class="ml-2" />
              </span>
            </div>
            <div v-if="currentOutputMessageContent" class="chat-message agent">
              <font-awesome-icon icon="desktop" class="mr-2" />
              {{ currentOutputMessageContent }}
            </div>
          </div>
          <div class="input-group mb-2">
            <input
              type="text"
              class="form-control"
              v-model="chatInput"
              @keydown.enter.exact.prevent="submitChat"
              placeholder="Digite sua mensagem..."
            />
            <div class="input-group-append">
              <button class="btn btn-primary" type="button" @click="submitChat">
                <font-awesome-icon icon="paper-plane" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./akip-chat.component.ts"></script>

<style scoped>
#chatBox {
  width: 80%;
  height: 70%;
  border: 1px solid #ccc;
  border-radius: 5px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
}

.black {
  color: black;
}

#chatContainer {
  overflow-y: auto;
  padding: 10px;
}

#chatArea {
  display: flex;
  flex-direction: column;
}

.chat-message {
  margin: 10px 0;
  padding: 10px;
  border-radius: 5px;
  max-width: 60%;
}

.chat-message.user {
  background-color: #42c27f;
  align-self: flex-end;
  border-radius: 5px 5px 0 5px;
}

.chat-message.agent {
  background-color: #499be3;
  align-self: flex-start;
  border-radius: 5px 5px 5px 0;
}

.input-group {
  margin-top: 10px;
}
</style>
