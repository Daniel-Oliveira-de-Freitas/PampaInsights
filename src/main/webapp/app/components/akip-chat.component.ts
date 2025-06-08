import { defineComponent, ref, onMounted, watch, nextTick } from 'vue';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import AkipAiService from '@/entities/akip-ai/akip-ai.service';
import { Message } from '@/shared/model/message.model';
import { useI18n } from 'vue-i18n';

export default defineComponent({
  components: { FontAwesomeIcon },
  setup() {
    const { t: t$ } = useI18n();
    const akipAiService = new AkipAiService();
    const chatInput = ref('');
    const messages = ref<Message[]>([]);
    const currentOutputMessageContent = ref('');
    const conversations = ref<any[]>([]);
    const selectedConversationId = ref('');

    onMounted(async () => {
      try {
        await retrieveConverations();
      } catch (error) {
        console.error('Error fetching conversations:', error);
      }
    });

    const retrieveConverations = async () => {
      akipAiService.getConversations().then(res => {
        conversations.value = res.data;
      });
    };

    const loadMessages = async (conversationId: string) => {
      selectedConversationId.value = conversationId;
      try {
        console.log(conversationId);
        const response = await akipAiService.getMessages(conversationId);
        messages.value = response.data;
      } catch (error) {
        console.error('Error loading messages:', error);
      }
    };

    const createNewConversation = async () => {
      akipAiService.createConversation().then(res => {
        const newConversation = res.data;
        selectedConversationId.value = newConversation.conversationId;
        messages.value = [new Message('agent', 'Olá, sou o LLM Llama 3. Como posso ajudar você??')];
      });
    };

    const submitChat = async () => {
      if (chatInput.value.trim()) {
        if (!selectedConversationId.value) {
          await createNewConversation();
        }

        const inputMessage = new Message('user', chatInput.value);
        messages.value.push(inputMessage);
        chatInput.value = '';

        try {
          const res = await akipAiService.sendMessage(inputMessage, selectedConversationId.value!);
          selectedConversationId.value = res.data.conversationId;

          // Simulação de streaming: mostra a resposta letra por letra
          const responseMessage = res.data.messages[res.data.messages.length - 1];
          const message = new Message('agent', '');
          messages.value.push(message);

          await streamText(responseMessage.content, message);

          retrieveConverations();
        } catch (error) {
          console.error('Error submitting chat:', error);
          messages.value.push(new Message('agent', 'Sorry, there was an error. Please try again later.'));
        }
      }
    };

    const streamText = async (text: string, message: Message, delay = 30) => {
      for (let i = 0; i < text.length; i++) {
        await new Promise(resolve => setTimeout(resolve, delay));
        message.content += text[i];
        messages.value = [...messages.value];
      }
    };

    watch(messages, async () => {
      await nextTick();
      const area = document.querySelector('#chatArea');
      if (area) {
        area.scrollTop = area.scrollHeight;
      }
    });

    return {
      chatInput,
      messages,
      currentOutputMessageContent,
      submitChat,
      conversations,
      selectedConversationId,
      loadMessages,
      createNewConversation,
      t$,
    };
  },
});
