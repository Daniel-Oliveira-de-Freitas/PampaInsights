import { defineComponent, nextTick, onMounted, ref } from 'vue';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import AkipAiService from '@/entities/akip-ai/akip-ai.service';
import { Message } from '@/shared/model/message.model';

export default defineComponent({
  components: { FontAwesomeIcon },
  setup() {
    const akipAiService = new AkipAiService();
    const chatInput = ref('');
    const messages = ref<Message[]>([]);
    const currentOutputMessageContent = ref('');
    const conversations = ref<any[]>([]);
    const selectedConversationId = ref('');
    const chatArea = ref<HTMLElement | null>(null);

    const scrollToBottom = async () => {
      await nextTick();
      if (chatArea.value) {
        chatArea.value.scrollTop = chatArea.value.scrollHeight;
      }
    };

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
        await scrollToBottom();
      } catch (error) {
        console.error('Error loading messages:', error);
      }
    };

    const createNewConversation = async () => {
      akipAiService.createConversation().then(res => {
        const newConversation = res.data;
        selectedConversationId.value = newConversation.conversationId;
        messages.value = [new Message('agent', 'Olá, eu sou o Pampa Insights AI. Como posso ajudar?')];
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
        currentOutputMessageContent.value = '';
        await scrollToBottom();

        try {
          await akipAiService.streamMessage(inputMessage, selectedConversationId.value!, token => {
            currentOutputMessageContent.value += token;
            scrollToBottom();
          });
          messages.value.push(new Message('agent', currentOutputMessageContent.value));
          currentOutputMessageContent.value = '';
          await scrollToBottom();
          retrieveConverations();
        } catch (error) {
          console.error('Error submitting chat:', error);
          currentOutputMessageContent.value = '';
          messages.value.push(new Message('agent', 'Desculpe, ocorreu um erro. Por favor tente mais tarde.'));
        }
      }
    };

    return {
      chatInput,
      messages,
      currentOutputMessageContent,
      submitChat,
      conversations,
      selectedConversationId,
      loadMessages,
      createNewConversation,
      chatArea,
    };
  },
});
