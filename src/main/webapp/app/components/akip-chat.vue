<template>
  <div id="chatBox" class="container mt-4">
    <div class="row h-100">
      <div id="conversationsList" class="col-3 p-3 border-right" style="background-color: #c8e6e6; border-radius: 8px 0 0 8px">
        <h5>Meus Chats</h5>
        <ul class="list-group mb-3">
          <li
            v-for="conversation in conversations"
            :key="conversation.conversationId"
            class="list-group-item list-group-item-action"
            @click="loadMessages(conversation.conversationId)"
          >
            {{ conversation.name }}
          </li>
        </ul>
        <button class="btn btn-primary btn-block" @click="createNewConversation">Novo Chat</button>
      </div>

      <div id="chatContainer" class="col-9 p-3 d-flex flex-column justify-content-between">
        <div
          id="chatArea"
          ref="chatArea"
          class="border rounded p-3 mb-3"
          style="height: 550px; overflow-y: scroll; background-color: #ffffff"
        >
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
            <button class="btn btn-primary" type="button" @click="submitChat">Enviar</button>
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
  height: 80%;
  border: 1px solid #b2d8d8;
  border-radius: 8px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #eaf6f6;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.1);
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
  background-color: #67ac8f;
  align-self: flex-end;
  border-radius: 5px 5px 0 5px;
}

.chat-message.agent {
  background-color: #69baff;
  align-self: flex-start;
  border-radius: 5px 5px 5px 0;
}

.input-group {
  margin-top: 10px;
}
</style>
